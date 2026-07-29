package com.hrms.payrevision.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.RevisionReason;  // ← Master se import
import com.hrms.master.infrastructure.RevisionReasonRepository;  // ← Master repository
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
    private final RevisionReasonRepository reasonRepo;  // ← Master repository
    private final PayRevisionMapper mapper;
    private final PdfPayRevisionLetterGenerator letterGenerator;
    private final PayRevisionDocumentStorageService storageService;

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

        // ✅ Master RevisionReason se find karo
        RevisionReason reason = reasonRepo.findById(req.getReasonId())
                .orElseThrow(() -> new RuntimeException("Pay revision reason not found"));

        r.setReason(reason);

        r.setEffectiveDate(req.getEffectiveDate() != null ? req.getEffectiveDate() : LocalDate.now());
        r.setRemarks(req.getRemarks());
        r.setIsActive(true);

        r.compute();

        PayRevisionRecord saved = payRevisionRepo.save(r);

        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = payRevisionRepo.save(saved);
        } catch (Exception e) {
            System.err.println("Failed to auto-generate pay revision letter for id " + saved.getId() + ": " + e.getMessage());
        }

        return mapper.toResponse(saved);
    }
}