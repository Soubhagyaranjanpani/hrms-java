package com.hrms.deputation.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.dto.CreateDeputationRequest;
import com.hrms.deputation.dto.DeputationRecordResponse;
import com.hrms.deputation.infrastructure.DeputationRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.DeputationType;
import com.hrms.master.infrastructure.DeputationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateDeputationUseCase {

    private final DeputationRepository deputationRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final DeputationTypeRepository deputationTypeRepo;
    private final DeputationMapper mapper;
    private final PdfDeputationLetterGenerator letterGenerator;
    private final DeputationDocumentStorageService storageService;
    private final AuditService auditService;

    public DeputationRecordResponse execute(CreateDeputationRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Fetch Deputation Type
        DeputationType deputationType = null;
        if (req.getDeputationTypeId() != null) {
            deputationType = deputationTypeRepo.findById(req.getDeputationTypeId())
                    .orElseThrow(() -> new RuntimeException("Deputation Type not found"));
        }

        // Fetch Reporting Authority
        EmployeeDesignation authority = employeeDesignationRepo.findById(req.getReportingAuthorityId())
                .orElseThrow(() -> new RuntimeException("Reporting authority not found"));

        DeputationRecord r = new DeputationRecord();
        r.setEmployee(emp);
        r.setDeputationOrderNumber(req.getDeputationOrderNumber());
        r.setDeputationOrganization(req.getDeputationOrganization());
        r.setDeputationType(deputationType);
        r.setReportingAuthority(authority);
        r.setStartDate(req.getStartDate() != null ? req.getStartDate() : LocalDate.now());
        r.setEndDate(req.getEndDate());
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

        DeputationRecord saved = deputationRepo.save(r);

        // Auto-generate deputation letter
        boolean letterGenerated = false;
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = deputationRepo.save(saved);
            letterGenerated = true;
        } catch (Exception e) {
            System.err.println("Failed to auto-generate deputation letter: " + e.getMessage());
        }

        // ── AUDIT LOGGING (Consolidated) ──

        // 1. Main deputation creation with all details
        String deputationDetails = "Organization: " + saved.getDeputationOrganization() +
                ", Type: " + (deputationType != null ? deputationType.toString() : "N/A") +
                ", Period: " + saved.getStartDate() + " to " +
                (saved.getEndDate() != null ? saved.getEndDate().toString() : "Ongoing");

        auditService.log(
                AuditModule.DEPUTATION,
                AuditAction.CREATE,
                "Deputation created for " + emp.getFullName() +
                        " (" + emp.getEmployeeCode() + ")",
                emp,
                "Deputation Record",
                null,
                deputationDetails,
                "Deputation order: " + saved.getDeputationOrderNumber() +
                        ", Reporting Authority: " + authority.toString() +
                        (req.getRemarks() != null ? ", Remarks: " + req.getRemarks() : ""),
                saved.getId()
        );

        // 2. Document generation audit
        if (letterGenerated) {
            auditService.log(
                    AuditModule.DOCUMENTS,
                    AuditAction.UPLOAD,
                    "Deputation letter generated: " + saved.getDocumentName(),
                    emp,
                    saved.getId()
            );
        }

        return mapper.toResponse(saved);
    }
}