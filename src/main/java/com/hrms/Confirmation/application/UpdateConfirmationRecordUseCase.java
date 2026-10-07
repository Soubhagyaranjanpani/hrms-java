package com.hrms.Confirmation.application;

import com.hrms.Confirmation.application.ConfirmationMapper;
import com.hrms.Confirmation.domain.ConfirmationRecord;
import com.hrms.Confirmation.dto.ConfirmationRecordResponse;
import com.hrms.Confirmation.dto.UpdateConfirmationRequest;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UpdateConfirmationRecordUseCase {

    private final ConfirmationRepository repo;
    private final ConfirmationMapper mapper;
    private final AuditService auditService;

    public ConfirmationRecordResponse execute(Long id, UpdateConfirmationRequest req) {
        ConfirmationRecord c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Confirmation record not found"));

        // Capture old values for audit
        String oldOrderNumber = c.getConfirmationOrderNumber();
        LocalDate oldConfirmationDate = c.getConfirmationDate();
        String oldRemarks = c.getRemarks();

        // Update fields
        if (req.getConfirmationOrderNumber() != null) c.setConfirmationOrderNumber(req.getConfirmationOrderNumber());
        if (req.getConfirmationDate() != null) c.setConfirmationDate(req.getConfirmationDate());
        if (req.getRemarks() != null) c.setRemarks(req.getRemarks());

        ConfirmationRecord updatedConfirmation = repo.save(c);

        // Audit Log - Track each field change

        // 1. Order Number change
        if (!oldOrderNumber.equals(updatedConfirmation.getConfirmationOrderNumber())) {
            auditService.log(
                    AuditModule.CONFIRMATION,
                    AuditAction.UPDATE,
                    "Confirmation order number updated: " +
                            oldOrderNumber + " → " + updatedConfirmation.getConfirmationOrderNumber(),
                    null,  // No employee if ConfirmationRecord doesn't have employee field
                    "Order Number",
                    oldOrderNumber,
                    updatedConfirmation.getConfirmationOrderNumber(),
                    "Confirmation order number changed",
                    updatedConfirmation.getId()
            );
        }

        // 2. Confirmation Date change
        if (oldConfirmationDate != null && !oldConfirmationDate.equals(updatedConfirmation.getConfirmationDate())) {
            auditService.log(
                    AuditModule.CONFIRMATION,
                    AuditAction.UPDATE,
                    "Confirmation date updated for order: " +
                            updatedConfirmation.getConfirmationOrderNumber(),
                    null,
                    "Confirmation Date",
                    oldConfirmationDate.toString(),
                    updatedConfirmation.getConfirmationDate().toString(),
                    "Confirmation date changed",
                    updatedConfirmation.getId()
            );
        }

        // 3. Remarks change
        if (oldRemarks != null && !oldRemarks.equals(updatedConfirmation.getRemarks())) {
            auditService.log(
                    AuditModule.CONFIRMATION,
                    AuditAction.UPDATE,
                    "Confirmation remarks updated for order: " +
                            updatedConfirmation.getConfirmationOrderNumber(),
                    null,
                    "Remarks",
                    oldRemarks,
                    updatedConfirmation.getRemarks(),
                    "Confirmation remarks changed",
                    updatedConfirmation.getId()
            );
        }

        return mapper.toResponse(updatedConfirmation);
    }
}