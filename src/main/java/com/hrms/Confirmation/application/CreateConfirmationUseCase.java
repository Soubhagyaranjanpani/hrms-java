package com.hrms.Confirmation.application;

import com.hrms.Confirmation.application.ConfirmationDocumentStorageService;
import com.hrms.Confirmation.application.ConfirmationMapper;
import com.hrms.Confirmation.application.PdfConfirmationLetterGenerator;
import com.hrms.Confirmation.domain.ConfirmationRecord;
import com.hrms.Confirmation.dto.ConfirmationRecordResponse;
import com.hrms.Confirmation.dto.CreateConfirmationRequest;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
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
    private final AuditService auditService;

    public ConfirmationRecordResponse execute(CreateConfirmationRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        ConfirmationRecord r = new ConfirmationRecord();
        r.setEmployee(emp);
        r.setConfirmationOrderNumber(req.getConfirmationOrderNumber());

        // ── Auto-populate department/designation from the employee's current record ──
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
        boolean letterGenerated = false;
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = confirmationRepo.save(saved);
            letterGenerated = true;
        } catch (Exception e) {
            System.err.println("Failed to auto-generate confirmation letter for id " + saved.getId() + ": " + e.getMessage());
        }

        // Audit Log - Main confirmation creation
        auditService.log(
                AuditModule.CONFIRMATION,
                AuditAction.CREATE,
                "Confirmation created for " + emp.getFullName() +
                        " (" + emp.getEmployeeCode() + ")",
                emp,  // Subject employee
                "Confirmation Record",
                null,
                "Order: " + saved.getConfirmationOrderNumber() +
                        ", Department: " + (saved.getDepartmentName() != null ? saved.getDepartmentName() : "N/A") +
                        ", Designation: " + (saved.getDesignationName() != null ? saved.getDesignationName() : "N/A"),
                "New confirmation record created" +
                        (req.getRemarks() != null ? ", Remarks: " + req.getRemarks() : ""),
                saved.getId()
        );

        // Audit Log - Document generation (if successful)
        if (letterGenerated) {
            auditService.log(
                    AuditModule.DOCUMENTS,
                    AuditAction.UPLOAD,
                    "Confirmation letter generated: " + saved.getDocumentName(),
                    emp,
                    "Document",
                    null,
                    saved.getDocumentName(),
                    "Auto-generated confirmation letter",
                    saved.getId()
            );
        }

        return mapper.toResponse(saved);
    }
}