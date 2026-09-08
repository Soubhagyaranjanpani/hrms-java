package com.hrms.retirement.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.PensionEligibility;
import com.hrms.master.domain.RetirementType;
import com.hrms.master.infrastructure.PensionEligibilityRepository;
import com.hrms.master.infrastructure.RetirementTypeRepository;
import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.dto.CreateRetirementRequest;
import com.hrms.retirement.dto.RetirementRecordResponse;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateRetirementUseCase {

    private final RetirementRepository retirementRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final RetirementTypeRepository retirementTypeRepo;
    private final PensionEligibilityRepository pensionEligibilityRepo;
    private final RetirementMapper mapper;
    private final PdfRetirementLetterGenerator letterGenerator;
    private final RetirementDocumentStorageService storageService;
    private final AuditService auditService;

    public RetirementRecordResponse execute(CreateRetirementRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        RetirementRecord r = new RetirementRecord();
        r.setEmployee(emp);

        // Retirement Type
        RetirementType type = retirementTypeRepo.findById(req.getRetirementTypeId())
                .orElseThrow(() -> new RuntimeException("Retirement type not found"));
        r.setRetirementType(type);

        // Pension Eligibility
        PensionEligibility eligibility = pensionEligibilityRepo.findById(req.getPensionEligibilityId())
                .orElseThrow(() -> new RuntimeException("Pension eligibility option not found"));
        r.setPensionEligibility(eligibility);

        // Auto-populate department/designation
        r.setDepartmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null);

        employeeDesignationRepo.findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId())
                .ifPresent(currentAssignment -> {
                    if (currentAssignment.getDesignation() != null) {
                        r.setDesignationName(currentAssignment.getDesignation().getName());
                    }
                });

        r.setRetirementDate(req.getRetirementDate() != null ? req.getRetirementDate() : LocalDate.now());
        r.setPensionNumber(req.getPensionNumber());
        r.setRetirementOrder(req.getRetirementOrder());
        r.setRetirementBenefits(req.getRetirementBenefits());
        r.setIsActive(true);

        RetirementRecord saved = retirementRepo.save(r);

        // Employee ko inactive mark karein
        emp.setIsRetirement(true);
        emp.setIsActive(false);
        empRepo.save(emp);

        // Auto-generate retirement letter
        boolean letterGenerated = false;
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = retirementRepo.save(saved);
            letterGenerated = true;
        } catch (Exception e) {
            System.err.println("Failed to auto-generate retirement letter for id " + saved.getId() + ": " + e.getMessage());
        }

        // ── AUDIT LOGGING (Consolidated) ──

        // 1. Main retirement creation with all details
        String retirementDetails = "Date: " + saved.getRetirementDate() +
                ", Type: " + type.toString() +
                ", Pension Eligibility: " + eligibility.toString() +
                ", Order: " + (saved.getRetirementOrder() != null ? saved.getRetirementOrder() : "N/A");

        auditService.log(
                AuditModule.RETIREMENT,
                AuditAction.CREATE,
                "Retirement record created for " + emp.getFullName() +
                        " (" + emp.getEmployeeCode() + ")",
                emp,
                "Retirement Record",
                null,
                retirementDetails,
                (saved.getPensionNumber() != null ? "Pension Number: " + saved.getPensionNumber() + ", " : "") +
                        (saved.getRetirementBenefits() != null ? "Benefits: " + saved.getRetirementBenefits() : ""),
                saved.getId()
        );

        // 2. Employee status change audit
        auditService.log(
                AuditModule.EMPLOYEE,
                AuditAction.UPDATE,
                "Employee marked as retired: " + emp.getFullName(),
                emp,
                "Employment Status",
                "Active",
                "Retired",
                "Employee retired on " + saved.getRetirementDate(),
                emp.getId()
        );

        // 3. Document generation audit
        if (letterGenerated) {
            auditService.log(
                    AuditModule.DOCUMENTS,
                    AuditAction.UPLOAD,
                    "Retirement letter generated: " + saved.getDocumentName(),
                    emp,
                    saved.getId()
            );
        }

        return mapper.toResponse(saved);
    }
}