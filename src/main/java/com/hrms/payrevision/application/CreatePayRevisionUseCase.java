package com.hrms.payrevision.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.RevisionReason;
import com.hrms.master.infrastructure.RevisionReasonRepository;
import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.dto.CreatePayRevisionRequest;
import com.hrms.payrevision.dto.PayRevisionRecordResponse;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePayRevisionUseCase {

    private final PayRevisionRepository payRevisionRepo;
    private final EmployeeRepository empRepo;
    private final RevisionReasonRepository reasonRepo;
    private final PayRevisionMapper mapper;
    private final PdfPayRevisionLetterGenerator letterGenerator;
    private final PayRevisionDocumentStorageService storageService;
    private final AuditService auditService;

    public PayRevisionRecordResponse execute(CreatePayRevisionRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        PayRevisionRecord r = new PayRevisionRecord();
        r.setEmployee(emp);
        r.setPayRevisionOrderNumber(req.getPayRevisionOrderNumber());

        Double prevMin = req.getPreviousPayScaleMin();
        Double prevMax = req.getPreviousPayScaleMax();
        if (prevMin == null || prevMax == null) {
            List<PayRevisionRecord> history = payRevisionRepo
                    .findByEmployee_IdAndIsDeletedFalseOrderByEffectiveDateDesc(emp.getId());
            if (!history.isEmpty()) {
                PayRevisionRecord last = history.get(0);
                if (prevMin == null) prevMin = last.getRevisedPayScaleMin();
                if (prevMax == null) prevMax = last.getRevisedPayScaleMax();
            }
        }
        r.setPreviousPayScaleMin(prevMin != null ? prevMin : 0.0);
        r.setPreviousPayScaleMax(prevMax != null ? prevMax : 0.0);

        r.setRevisedPayScaleMin(req.getRevisedPayScaleMin());
        r.setRevisedPayScaleMax(req.getRevisedPayScaleMax());

        // Master RevisionReason se find karo
        RevisionReason reason = reasonRepo.findById(req.getReasonId())
                .orElseThrow(() -> new RuntimeException("Pay revision reason not found"));

        r.setReason(reason);

        r.setEffectiveDate(req.getEffectiveDate() != null ? req.getEffectiveDate() : LocalDate.now());
        r.setRemarks(req.getRemarks());
        r.setIsActive(true);

        r.compute();

        PayRevisionRecord saved = payRevisionRepo.save(r);

        // Auto-generate pay revision letter
        boolean letterGenerated = false;
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = payRevisionRepo.save(saved);
            letterGenerated = true;
        } catch (Exception e) {
            System.err.println("Failed to auto-generate pay revision letter for id " + saved.getId() + ": " + e.getMessage());
        }

        // ── AUDIT LOGGING ──

        // 1. Main pay revision creation audit
        auditService.log(
                AuditModule.PAY_REVISION,
                AuditAction.CREATE,
                "Pay revision created for " + emp.getFullName() +
                        " (" + emp.getEmployeeCode() + ")",
                emp,
                "Pay Revision Record",
                null,
                "Order: " + saved.getPayRevisionOrderNumber(),
                "Pay scale revised from " + saved.getPreviousPayScaleMin() + "-" +
                        saved.getPreviousPayScaleMax() + " to " +
                        saved.getRevisedPayScaleMin() + "-" + saved.getRevisedPayScaleMax() +
                        ", Reason: " + reason.toString() +
                        ", Effective Date: " + saved.getEffectiveDate() +
                        (req.getRemarks() != null ? ", Remarks: " + req.getRemarks() : ""),
                saved.getId()
        );

        // 2. Pay scale change audit
        auditService.log(
                AuditModule.PAY_REVISION,
                AuditAction.UPDATE,
                "Pay scale revised for " + emp.getFullName(),
                emp,
                "Pay Scale",
                saved.getPreviousPayScaleMin() + "-" + saved.getPreviousPayScaleMax(),
                saved.getRevisedPayScaleMin() + "-" + saved.getRevisedPayScaleMax(),
                "Pay revision order: " + saved.getPayRevisionOrderNumber(),
                saved.getId()
        );

        // 3. Document generation audit (if successful)
        if (letterGenerated) {
            auditService.log(
                    AuditModule.DOCUMENTS,
                    AuditAction.UPLOAD,
                    "Pay revision letter generated: " + saved.getDocumentName(),
                    emp,
                    "Document",
                    null,
                    saved.getDocumentName(),
                    "Auto-generated pay revision letter",
                    saved.getId()
            );
        }

        return mapper.toResponse(saved);
    }
}