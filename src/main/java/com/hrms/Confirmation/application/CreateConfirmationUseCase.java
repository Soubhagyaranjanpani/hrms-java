package com.hrms.confirmation.application;

import com.hrms.confirmation.domain.ConfirmationRecord;
import com.hrms.confirmation.dto.ConfirmationRecordResponse;
import com.hrms.confirmation.dto.CreateConfirmationRequest;
import com.hrms.confirmation.infrastructure.ConfirmationRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateConfirmationUseCase {

    private final ConfirmationRepository confirmationRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final ConfirmationMapper mapper;
    private final PdfConfirmationLetterGenerator letterGenerator;
    private final ConfirmationDocumentStorageService storageService;

    public ConfirmationRecordResponse execute(CreateConfirmationRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        ConfirmationRecord r = new ConfirmationRecord();
        r.setEmployee(emp);
        r.setConfirmationOrderNumber(req.getConfirmationOrderNumber());

        // ── Auto-populate department/designation from the employee's current record ──
        // The form shows these as read-only "Auto-populated" fields, so they're snapshotted
        // here rather than taken from the request.
        r.setDepartmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null);

        employeeDesignationRepo.findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId())
                .ifPresent(currentAssignment -> {
                    if (currentAssignment.getDesignation() != null) {
                        r.setDesignationName(currentAssignment.getDesignation().getName());
                    }
                });

        // ── Confirmed By (authority) ──
        EmployeeDesignation confirmedBy = employeeDesignationRepo.findById(req.getConfirmedById())
                .orElseThrow(() -> new RuntimeException("Confirming authority not found"));
        r.setConfirmedBy(confirmedBy);

        r.setConfirmationDate(req.getConfirmationDate() != null ? req.getConfirmationDate() : LocalDate.now());
        r.setRemarks(req.getRemarks());
        r.setIsActive(true);

        ConfirmationRecord saved = confirmationRepo.save(r);

        // Auto-generate the confirmation letter and persist its path/name on the record
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = confirmationRepo.save(saved);
        } catch (Exception e) {
            System.err.println("Failed to auto-generate confirmation letter for id " + saved.getId() + ": " + e.getMessage());
        }

        return mapper.toResponse(saved);
    }
}
