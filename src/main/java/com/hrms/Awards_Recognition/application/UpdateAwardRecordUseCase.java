package com.hrms.Awards_Recognition.application;

import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Awards_Recognition.dto.AwardRecordResponse;
import com.hrms.Awards_Recognition.dto.UpdateAwardRequest;
import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.master.infrastructure.AwardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateAwardRecordUseCase {

    private final AwardRepository repo;
    private final AwardMapper mapper;
    private final AwardTypeRepository awardTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final AuditService auditService;

    @Transactional
    public AwardRecordResponse execute(Long id, UpdateAwardRequest req) {
        AwardRecord a = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Award record not found with ID: " + id));

        // Capture old values for audit
        String oldAwardName = a.getAwardName();
        String oldDescription = a.getDescription();
        String oldAwardType = a.getAwardType() != null ? a.getAwardType().toString() : null;
        String oldIssuedBy = a.getIssuedBy() != null ? a.getIssuedBy().toString() : null;

        // Update fields
        if (req.getAwardName() != null) {
            a.setAwardName(req.getAwardName());
        }
        if (req.getAwardDate() != null) {
            a.setAwardDate(req.getAwardDate());
        }
        if (req.getDescription() != null) {
            a.setDescription(req.getDescription());
        }

        // Update Award Type
        if (req.getAwardTypeId() != null) {
            var awardType = awardTypeRepo.findById(req.getAwardTypeId())
                    .orElseThrow(() -> new RuntimeException("Award Type not found with ID: " + req.getAwardTypeId()));
            a.setAwardType(awardType);
        }

        // Update Issued By
        if (req.getIssuedById() != null) {
            var issuedBy = employeeDesignationRepo.findById(req.getIssuedById())
                    .orElseThrow(() -> new RuntimeException("Issued By not found with ID: " + req.getIssuedById()));
            a.setIssuedBy(issuedBy);
        }

        AwardRecord updatedAward = repo.save(a);

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (oldAwardName != null && !oldAwardName.equals(updatedAward.getAwardName())) {
            changes.add("Award Name: " + oldAwardName + " → " + updatedAward.getAwardName());
        }

        if (req.getAwardDate() != null) {
            changes.add("Award Date updated");
        }

        if (oldDescription != null && !oldDescription.equals(updatedAward.getDescription())) {
            changes.add("Description updated");
        }

        String newAwardType = updatedAward.getAwardType() != null ?
                updatedAward.getAwardType().toString() : null;
        if (oldAwardType != null && !oldAwardType.equals(newAwardType)) {
            changes.add("Award Type: " + oldAwardType + " → " + newAwardType);
        }

        String newIssuedBy = updatedAward.getIssuedBy() != null ?
                updatedAward.getIssuedBy().toString() : null;
        if (oldIssuedBy != null && !oldIssuedBy.equals(newIssuedBy)) {
            changes.add("Issued By: " + oldIssuedBy + " → " + newIssuedBy);
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.AWARD,
                    AuditAction.UPDATE,
                    "Award record updated for " +
                            (updatedAward.getEmployee() != null ?
                                    updatedAward.getEmployee().getFullName() +
                                    " (" + updatedAward.getEmployee().getEmployeeCode() + ")" :
                                    "record"),
                    updatedAward.getEmployee(),
                    "Multiple Fields",
                    oldAwardName,
                    updatedAward.getAwardName(),
                    String.join("; ", changes),
                    updatedAward.getId()
            );
        }

        return mapper.toResponse(updatedAward);
    }
}