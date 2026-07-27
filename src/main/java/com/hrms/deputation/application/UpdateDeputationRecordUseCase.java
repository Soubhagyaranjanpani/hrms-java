package com.hrms.deputation.application;

import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.dto.DeputationRecordResponse;
import com.hrms.deputation.dto.UpdateDeputationRequest;
import com.hrms.deputation.infrastructure.DeputationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDeputationRecordUseCase {

    private final DeputationRepository repo;
    private final DeputationMapper mapper;

    public DeputationRecordResponse execute(Long id, UpdateDeputationRequest req) {
        DeputationRecord d = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Deputation record not found"));

        if (req.getDeputationOrderNumber() != null) d.setDeputationOrderNumber(req.getDeputationOrderNumber());
        if (req.getDeputationOrganization() != null) d.setDeputationOrganization(req.getDeputationOrganization());
        if (req.getStartDate() != null) d.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) d.setEndDate(req.getEndDate());
        if (req.getDeputationType() != null) d.setDeputationType(req.getDeputationType());

        return mapper.toResponse(repo.save(d));
    }
}
