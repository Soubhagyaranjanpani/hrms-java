package com.hrms.deputation.application;

import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.dto.DeputationRecordResponse;
import com.hrms.deputation.dto.UpdateDeputationRequest;
import com.hrms.deputation.infrastructure.DeputationRepository;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.master.infrastructure.DeputationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDeputationRecordUseCase {

    private final DeputationRepository repo;
    private final DeputationMapper mapper;
    private final DeputationTypeRepository deputationTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;

    public DeputationRecordResponse execute(Long id, UpdateDeputationRequest req) {
        DeputationRecord d = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Deputation record not found"));

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

        return mapper.toResponse(repo.save(d));
    }
}