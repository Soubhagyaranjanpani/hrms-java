package com.hrms.payrevision.application;

import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.dto.PayRevisionRecordResponse;
import com.hrms.payrevision.dto.UpdatePayRevisionRequest;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePayRevisionRecordUseCase {

    private final PayRevisionRepository repo;
    private final PayRevisionMapper mapper;

    public PayRevisionRecordResponse execute(Long id, UpdatePayRevisionRequest req) {
        PayRevisionRecord p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pay revision record not found"));

        if (req.getPayRevisionOrderNumber() != null) p.setPayRevisionOrderNumber(req.getPayRevisionOrderNumber());
        if (req.getEffectiveDate() != null) p.setEffectiveDate(req.getEffectiveDate());
        if (req.getRevisedPayScaleMin() != null) p.setRevisedPayScaleMin(req.getRevisedPayScaleMin());
        if (req.getRevisedPayScaleMax() != null) p.setRevisedPayScaleMax(req.getRevisedPayScaleMax());
        if (req.getRemarks() != null) p.setRemarks(req.getRemarks());

        return mapper.toResponse(repo.save(p));
    }
}
