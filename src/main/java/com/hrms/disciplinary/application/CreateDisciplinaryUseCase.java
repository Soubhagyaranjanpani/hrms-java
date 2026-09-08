package com.hrms.disciplinary.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.disciplinary.domain.DisciplinaryRecord;
import com.hrms.disciplinary.dto.CreateDisciplinaryRequest;
import com.hrms.disciplinary.dto.DisciplinaryRecordResponse;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.ActionType;
import com.hrms.master.domain.PenaltyType;
import com.hrms.master.infrastructure.ActionTypeRepository;
import com.hrms.master.infrastructure.PenaltyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateDisciplinaryUseCase {

    private final DisciplinaryRepository disciplinaryRepo;
    private final EmployeeRepository empRepo;
    private final ActionTypeRepository actionTypeRepo;
    private final PenaltyTypeRepository penaltyTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final DisciplinaryMapper mapper;
    private final PdfDisciplinaryLetterGenerator letterGenerator;
    private final DisciplinaryDocumentStorageService storageService;
    private final AuditService auditService;

    @Transactional
    public DisciplinaryRecordResponse execute(CreateDisciplinaryRequest req) {
        // 1. Fetch Employee
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + req.getEmployeeId()));

        // 2. Fetch Action Type
        ActionType actionType = null;
        if (req.getActionTypeId() != null) {
            actionType = actionTypeRepo.findById(req.getActionTypeId())
                    .orElseThrow(() -> new RuntimeException("Action Type not found with ID: " + req.getActionTypeId()));
        }

        // 3. Fetch Penalty Type
        PenaltyType penaltyType = null;
        if (req.getPenaltyTypeId() != null) {
            penaltyType = penaltyTypeRepo.findById(req.getPenaltyTypeId())
                    .orElseThrow(() -> new RuntimeException("Penalty Type not found with ID: " + req.getPenaltyTypeId()));
        }

        // 4. Fetch Investigation Officer
        EmployeeDesignation officer = null;
        if (req.getInvestigationOfficerId() != null) {
            officer = employeeDesignationRepo.findById(req.getInvestigationOfficerId())
                    .orElseThrow(() -> new RuntimeException("Investigation Officer not found with ID: " + req.getInvestigationOfficerId()));
        }

        // 5. Create Disciplinary Record
        DisciplinaryRecord r = new DisciplinaryRecord();
        r.setEmployee(emp);
        r.setCaseNumber(req.getCaseNumber());
        r.setIncidentDate(req.getIncidentDate() != null ? req.getIncidentDate() : LocalDate.now());
        r.setActionType(actionType);
        r.setPenaltyType(penaltyType);
        r.setInvestigationOfficer(officer);
        r.setResolutionDate(req.getResolutionDate());
        r.setRemarks(req.getRemarks());

        // Auto-populate department/designation from employee
        r.setDepartmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null);

        employeeDesignationRepo.findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId())
                .ifPresent(currentAssignment -> {
                    if (currentAssignment.getDesignation() != null) {
                        r.setDesignationName(currentAssignment.getDesignation().getName());
                    }
                });

        r.setIsActive(true);

        // 6. Save
        DisciplinaryRecord saved = disciplinaryRepo.save(r);

        // 7. Auto-generate disciplinary letter
        boolean letterGenerated = false;
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = disciplinaryRepo.save(saved);
            letterGenerated = true;
        } catch (Exception e) {
            System.err.println("Failed to auto-generate disciplinary letter: " + e.getMessage());
        }

        // ── AUDIT LOGGING (Consolidated) ──

        // Main disciplinary record creation with all details
        String disciplinaryDetails = "Case: " + saved.getCaseNumber() +
                ", Action: " + (actionType != null ? actionType.toString() : "N/A") +
                ", Penalty: " + (penaltyType != null ? penaltyType.toString() : "N/A") +
                ", Officer: " + (officer != null ? officer.toString() : "N/A");

        auditService.log(
                AuditModule.DISCIPLINARY,
                AuditAction.CREATE,
                "Disciplinary record created for " + emp.getFullName() +
                        " (" + emp.getEmployeeCode() + ")",
                emp,
                "Disciplinary Record",
                null,
                disciplinaryDetails,
                "Incident Date: " + saved.getIncidentDate() +
                        (saved.getResolutionDate() != null ? ", Resolution Date: " + saved.getResolutionDate() : "") +
                        (req.getRemarks() != null ? ", Remarks: " + req.getRemarks() : ""),
                saved.getId()
        );

        // Document generation audit
        if (letterGenerated) {
            auditService.log(
                    AuditModule.DOCUMENTS,
                    AuditAction.UPLOAD,
                    "Disciplinary letter generated: " + saved.getDocumentName(),
                    emp,
                    saved.getId()
            );
        }

        return mapper.toResponse(saved);
    }
}