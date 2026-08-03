package com.hrms.retirement.application;

import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.dto.RetirementRecordResponse;
import com.hrms.retirement.dto.UpdateRetirementRequest;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateRetirementRecordUseCase {

    private final RetirementRepository repo;
    private final RetirementMapper mapper;

    public RetirementRecordResponse execute(Long id, UpdateRetirementRequest req) {
        RetirementRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Retirement record not found"));

        if (req.getRetirementDate() != null) r.setRetirementDate(req.getRetirementDate());
        if (req.getPensionNumber() != null) r.setPensionNumber(req.getPensionNumber());
        if (req.getRetirementOrder() != null) r.setRetirementOrder(req.getRetirementOrder());
        if (req.getRetirementBenefits() != null) r.setRetirementBenefits(req.getRetirementBenefits());

        return mapper.toResponse(repo.save(r));
    }
}
