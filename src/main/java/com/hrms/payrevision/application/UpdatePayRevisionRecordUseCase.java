package com.hrms.payrevision.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.dto.PayRevisionRecordResponse;
import com.hrms.payrevision.dto.UpdatePayRevisionRequest;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePayRevisionRecordUseCase {

    private final PayRevisionRepository repo;
    private final PayRevisionMapper mapper;
    private final AuditService auditService;

    public PayRevisionRecordResponse execute(Long id, UpdatePayRevisionRequest req) {
        PayRevisionRecord p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pay revision record not found"));

        // Capture old values for audit
        String oldOrderNumber = p.getPayRevisionOrderNumber();
        Double oldRevisedPayScaleMin = p.getRevisedPayScaleMin();
        Double oldRevisedPayScaleMax = p.getRevisedPayScaleMax();
        String oldRemarks = p.getRemarks();

        // Update fields
        if (req.getPayRevisionOrderNumber() != null) p.setPayRevisionOrderNumber(req.getPayRevisionOrderNumber());
        if (req.getEffectiveDate() != null) p.setEffectiveDate(req.getEffectiveDate());
        if (req.getRevisedPayScaleMin() != null) p.setRevisedPayScaleMin(req.getRevisedPayScaleMin());
        if (req.getRevisedPayScaleMax() != null) p.setRevisedPayScaleMax(req.getRevisedPayScaleMax());
        if (req.getRemarks() != null) p.setRemarks(req.getRemarks());

        PayRevisionRecord updatedPayRevision = repo.save(p);

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (oldOrderNumber != null && !oldOrderNumber.equals(updatedPayRevision.getPayRevisionOrderNumber())) {
            changes.add("Order Number: " + oldOrderNumber + " → " +
                    updatedPayRevision.getPayRevisionOrderNumber());
        }

        if (req.getEffectiveDate() != null) {
            changes.add("Effective Date updated");
        }

        if (oldRevisedPayScaleMin != null && !oldRevisedPayScaleMin.equals(updatedPayRevision.getRevisedPayScaleMin())) {
            changes.add("Pay Scale Min: " + oldRevisedPayScaleMin + " → " +
                    updatedPayRevision.getRevisedPayScaleMin());
        }

        if (oldRevisedPayScaleMax != null && !oldRevisedPayScaleMax.equals(updatedPayRevision.getRevisedPayScaleMax())) {
            changes.add("Pay Scale Max: " + oldRevisedPayScaleMax + " → " +
                    updatedPayRevision.getRevisedPayScaleMax());
        }

        if (oldRemarks != null && !oldRemarks.equals(updatedPayRevision.getRemarks())) {
            changes.add("Remarks updated");
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.PAY_REVISION,
                    AuditAction.UPDATE,
                    "Pay revision record updated for " +
                            (updatedPayRevision.getEmployee() != null ?
                                    updatedPayRevision.getEmployee().getFullName() +
                                    " (" + updatedPayRevision.getEmployee().getEmployeeCode() + ")" :
                                    "record"),
                    updatedPayRevision.getEmployee(),
                    "Multiple Fields",
                    oldOrderNumber,
                    updatedPayRevision.getPayRevisionOrderNumber(),
                    String.join("; ", changes),
                    updatedPayRevision.getId()
            );
        }

        return mapper.toResponse(updatedPayRevision);
    }
}