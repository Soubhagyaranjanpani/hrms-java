package com.hrms.deputation.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.dto.DeputationRecordResponse;
import com.hrms.deputation.dto.UpdateDeputationRequest;
import com.hrms.deputation.infrastructure.DeputationRepository;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.master.infrastructure.DeputationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateDeputationRecordUseCase {

    private final DeputationRepository repo;
    private final DeputationMapper mapper;
    private final DeputationTypeRepository deputationTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final AuditService auditService;

    public DeputationRecordResponse execute(Long id, UpdateDeputationRequest req) {
        DeputationRecord d = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Deputation record not found"));

        // Capture old values for audit
        String oldOrderNumber = d.getDeputationOrderNumber();
        String oldOrganization = d.getDeputationOrganization();
        String oldDeputationType = d.getDeputationType() != null ?
                d.getDeputationType().toString() : null;
        String oldReportingAuthority = d.getReportingAuthority() != null ?
                d.getReportingAuthority().toString() : null;
        String oldRemarks = d.getRemarks();

        // Update fields
        if (req.getDeputationOrderNumber() != null) {
            d.setDeputationOrderNumber(req.getDeputationOrderNumber());
        }
        if (req.getDeputationOrganization() != null) {
            d.setDeputationOrganization(req.getDeputationOrganization());
        }
        if (req.getStartDate() != null) {
            d.setStartDate(req.getStartDate());
        }
        if (req.getEndDate() != null) {
            d.setEndDate(req.getEndDate());
        }
        if (req.getDeputationTypeId() != null) {
            var type = deputationTypeRepo.findById(req.getDeputationTypeId())
                    .orElseThrow(() -> new RuntimeException("Deputation Type not found"));
            d.setDeputationType(type);
        }
        if (req.getReportingAuthorityId() != null) {
            var authority = employeeDesignationRepo.findById(req.getReportingAuthorityId())
                    .orElseThrow(() -> new RuntimeException("Reporting authority not found"));
            d.setReportingAuthority(authority);
        }
        if (req.getRemarks() != null) {
            d.setRemarks(req.getRemarks());
        }

        DeputationRecord updatedDeputation = repo.save(d);

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (oldOrderNumber != null && !oldOrderNumber.equals(updatedDeputation.getDeputationOrderNumber())) {
            changes.add("Order Number: " + oldOrderNumber + " → " +
                    updatedDeputation.getDeputationOrderNumber());
        }

        if (oldOrganization != null && !oldOrganization.equals(updatedDeputation.getDeputationOrganization())) {
            changes.add("Organization: " + oldOrganization + " → " +
                    updatedDeputation.getDeputationOrganization());
        }

        if (req.getStartDate() != null) {
            changes.add("Start Date updated");
        }

        if (req.getEndDate() != null) {
            changes.add("End Date updated");
        }

        String newDeputationType = updatedDeputation.getDeputationType() != null ?
                updatedDeputation.getDeputationType().toString() : null;
        if (oldDeputationType != null && !oldDeputationType.equals(newDeputationType)) {
            changes.add("Type: " + oldDeputationType + " → " + newDeputationType);
        }

        String newReportingAuthority = updatedDeputation.getReportingAuthority() != null ?
                updatedDeputation.getReportingAuthority().toString() : null;
        if (oldReportingAuthority != null && !oldReportingAuthority.equals(newReportingAuthority)) {
            changes.add("Authority: " + oldReportingAuthority + " → " + newReportingAuthority);
        }

        if (oldRemarks != null && !oldRemarks.equals(updatedDeputation.getRemarks())) {
            changes.add("Remarks updated");
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.DEPUTATION,
                    AuditAction.UPDATE,
                    "Deputation record updated for " +
                            (updatedDeputation.getEmployee() != null ?
                                    updatedDeputation.getEmployee().getFullName() +
                                    " (" + updatedDeputation.getEmployee().getEmployeeCode() + ")" :
                                    "record"),
                    updatedDeputation.getEmployee(),
                    "Multiple Fields",
                    oldOrderNumber,
                    updatedDeputation.getDeputationOrderNumber(),
                    String.join("; ", changes),
                    updatedDeputation.getId()
            );
        }

        return mapper.toResponse(updatedDeputation);
    }
}