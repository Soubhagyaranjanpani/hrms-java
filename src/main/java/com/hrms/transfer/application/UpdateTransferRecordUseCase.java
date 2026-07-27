package com.hrms.transfer.application;

import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.dto.TransferRecordResponse;
import com.hrms.transfer.dto.UpdateTransferRequest;
import com.hrms.transfer.infrastructure.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTransferRecordUseCase {

    private final TransferRepository repo;
    private final TransferMapper mapper;

    public TransferRecordResponse execute(Long id, UpdateTransferRequest req) {
        TransferRecord t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer record not found"));

        if (req.getTransferOrderNumber() != null) t.setTransferOrderNumber(req.getTransferOrderNumber());
        if (req.getTransferDate() != null) t.setTransferDate(req.getTransferDate());
        if (req.getTransferType() != null) t.setTransferType(req.getTransferType());
        if (req.getEffectiveDate() != null) t.setEffectiveDate(req.getEffectiveDate());
        if (req.getTransferReason() != null) t.setTransferReason(req.getTransferReason());

        return mapper.toResponse(repo.save(t));
    }
}
