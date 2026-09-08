package com.hrms.transfer.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.master.infrastructure.TransferTypeRepository;
import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.dto.TransferRecordResponse;
import com.hrms.transfer.dto.UpdateTransferRequest;
import com.hrms.transfer.infrastructure.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateTransferRecordUseCase {

    private final TransferRepository repo;
    private final TransferMapper mapper;
    private final TransferTypeRepository transferTypeRepo;
    private final AuditService auditService;

    public TransferRecordResponse execute(Long id, UpdateTransferRequest req) {
        TransferRecord t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer record not found"));

        // Capture old values for audit
        String oldOrderNumber = t.getTransferOrderNumber();
        String oldTransferReason = t.getTransferReason();
        String oldTransferType = t.getTransferType() != null ?
                t.getTransferType().toString() : null;

        // Update fields
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

        // Update TransferType if ID is provided
        if (req.getTransferTypeId() != null) {
            var transferType = transferTypeRepo.findById(req.getTransferTypeId())
                    .orElseThrow(() -> new RuntimeException("Transfer Type not found with ID: " + req.getTransferTypeId()));
            t.setTransferType(transferType);
        }

        TransferRecord updatedTransfer = repo.save(t);

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (oldOrderNumber != null && !oldOrderNumber.equals(updatedTransfer.getTransferOrderNumber())) {
            changes.add("Order Number: " + oldOrderNumber + " → " +
                    updatedTransfer.getTransferOrderNumber());
        }

        if (req.getTransferDate() != null) {
            changes.add("Transfer Date updated");
        }

        if (req.getEffectiveDate() != null) {
            changes.add("Effective Date updated");
        }

        if (oldTransferReason != null && !oldTransferReason.equals(updatedTransfer.getTransferReason())) {
            changes.add("Reason: " + oldTransferReason + " → " +
                    updatedTransfer.getTransferReason());
        }

        String newTransferType = updatedTransfer.getTransferType() != null ?
                updatedTransfer.getTransferType().toString() : null;
        if (oldTransferType != null && !oldTransferType.equals(newTransferType)) {
            changes.add("Type: " + oldTransferType + " → " + newTransferType);
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.TRANSFER,
                    AuditAction.UPDATE,
                    "Transfer record updated for " +
                            (updatedTransfer.getEmployee() != null ?
                                    updatedTransfer.getEmployee().getFullName() +
                                    " (" + updatedTransfer.getEmployee().getEmployeeCode() + ")" :
                                    "record"),
                    updatedTransfer.getEmployee(),
                    "Multiple Fields",
                    oldOrderNumber,
                    updatedTransfer.getTransferOrderNumber(),
                    String.join("; ", changes),
                    updatedTransfer.getId()
            );
        }

        return mapper.toResponse(updatedTransfer);
    }
}