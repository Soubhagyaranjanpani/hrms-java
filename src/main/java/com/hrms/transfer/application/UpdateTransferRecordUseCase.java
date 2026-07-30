package com.hrms.transfer.application;

import com.hrms.master.infrastructure.TransferTypeRepository;
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
    private final TransferTypeRepository transferTypeRepo;  // ✅ NEW

    public TransferRecordResponse execute(Long id, UpdateTransferRequest req) {
        TransferRecord t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer record not found"));

        if (req.getTransferOrderNumber() != null) {
            t.setTransferOrderNumber(req.getTransferOrderNumber());
        }
        if (req.getTransferDate() != null) {
            t.setTransferDate(req.getTransferDate());
        }
        if (req.getEffectiveDate() != null) {
            t.setEffectiveDate(req.getEffectiveDate());
        }
        if (req.getTransferReason() != null) {
            t.setTransferReason(req.getTransferReason());
        }

        // ✅ Update TransferType if ID is provided
        if (req.getTransferTypeId() != null) {
            var transferType = transferTypeRepo.findById(req.getTransferTypeId())
                    .orElseThrow(() -> new RuntimeException("Transfer Type not found with ID: " + req.getTransferTypeId()));
            t.setTransferType(transferType);
        }

        return mapper.toResponse(repo.save(t));
    }
}