package com.hrms.promotion.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.dto.PromotionRecordResponse;
import com.hrms.promotion.dto.UpdatePromotionRequest;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePromotionRecordUseCase {

    private final PromotionRepository repo;
    private final PromotionMapper mapper;
    private final AuditService auditService;

    public PromotionRecordResponse execute(Long id, UpdatePromotionRequest req) {
        PromotionRecord p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion record not found"));

        // Capture old values for audit
        String oldOrderNumber = p.getPromotionOrderNumber();
        Double oldSalary = p.getOldSalary();
        Double newSalary = p.getNewSalary();
        String oldRemarks = p.getRemarks();

        // Update fields
        if (req.getPromotionOrderNumber() != null) p.setPromotionOrderNumber(req.getPromotionOrderNumber());
        if (req.getOldSalary() != null) p.setOldSalary(req.getOldSalary());
        if (req.getNewSalary() != null) p.setNewSalary(req.getNewSalary());
        if (req.getPromotionDate() != null) p.setPromotionDate(req.getPromotionDate());
        if (req.getEffectiveDate() != null) p.setEffectiveDate(req.getEffectiveDate());
        if (req.getRemarks() != null) p.setRemarks(req.getRemarks());

        PromotionRecord updatedPromotion = repo.save(p);

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (oldOrderNumber != null && !oldOrderNumber.equals(updatedPromotion.getPromotionOrderNumber())) {
            changes.add("Order Number: " + oldOrderNumber + " → " +
                    updatedPromotion.getPromotionOrderNumber());
        }

        if (oldSalary != null && !oldSalary.equals(updatedPromotion.getOldSalary())) {
            changes.add("Old Salary: " + oldSalary + " → " +
                    updatedPromotion.getOldSalary());
        }

        if (newSalary != null && !newSalary.equals(updatedPromotion.getNewSalary())) {
            changes.add("New Salary: " + newSalary + " → " +
                    updatedPromotion.getNewSalary());
        }

        if (req.getPromotionDate() != null) {
            changes.add("Promotion Date updated");
        }

        if (req.getEffectiveDate() != null) {
            changes.add("Effective Date updated");
        }

        if (oldRemarks != null && !oldRemarks.equals(updatedPromotion.getRemarks())) {
            changes.add("Remarks updated");
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.PROMOTION,
                    AuditAction.UPDATE,
                    "Promotion record updated for " +
                            (updatedPromotion.getEmployee() != null ?
                                    updatedPromotion.getEmployee().getFullName() +
                                    " (" + updatedPromotion.getEmployee().getEmployeeCode() + ")" :
                                    "record"),
                    updatedPromotion.getEmployee(),
                    "Multiple Fields",
                    oldOrderNumber,
                    updatedPromotion.getPromotionOrderNumber(),
                    String.join("; ", changes),
                    updatedPromotion.getId()
            );
        }

        return mapper.toResponse(updatedPromotion);
    }
}