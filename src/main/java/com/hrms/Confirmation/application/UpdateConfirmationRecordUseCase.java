package com.hrms.Confirmation.application;

import com.hrms.Confirmation.application.ConfirmationMapper;
import com.hrms.Confirmation.domain.ConfirmationRecord;
import com.hrms.Confirmation.dto.ConfirmationRecordResponse;
import com.hrms.Confirmation.dto.UpdateConfirmationRequest;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateConfirmationRecordUseCase {

    private final ConfirmationRepository repo;
    private final ConfirmationMapper mapper;

    public ConfirmationRecordResponse execute(Long id, UpdateConfirmationRequest req) {
        ConfirmationRecord c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Confirmation record not found"));

        if (req.getConfirmationOrderNumber() != null) c.setConfirmationOrderNumber(req.getConfirmationOrderNumber());
        if (req.getConfirmationDate() != null) c.setConfirmationDate(req.getConfirmationDate());
        if (req.getRemarks() != null) c.setRemarks(req.getRemarks());

        return mapper.toResponse(repo.save(c));
    }
}
