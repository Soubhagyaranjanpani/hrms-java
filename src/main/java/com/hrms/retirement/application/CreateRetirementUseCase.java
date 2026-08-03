package com.hrms.retirement.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
// ── ASSUMPTION: adjust these imports if your existing master repositories live
// elsewhere or are named differently.
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

    public RetirementRecordResponse execute(CreateRetirementRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        RetirementRecord r = new RetirementRecord();
        r.setEmployee(emp);

        // ── Retirement Type (master dropdown) ──
        RetirementType type = retirementTypeRepo.findById(req.getRetirementTypeId())
                .orElseThrow(() -> new RuntimeException("Retirement type not found"));
        r.setRetirementType(type);

        // ── Pension Eligibility (master dropdown) ──
        PensionEligibility eligibility = pensionEligibilityRepo.findById(req.getPensionEligibilityId())
                .orElseThrow(() -> new RuntimeException("Pension eligibility option not found"));
        r.setPensionEligibility(eligibility);

        // ── Auto-populate department/designation from the employee's current record ──
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

        // Auto-generate the retirement order/letter and persist its path/name on the record
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = retirementRepo.save(saved);
        } catch (Exception e) {
            System.err.println("Failed to auto-generate retirement letter for id " + saved.getId() + ": " + e.getMessage());
        }

        return mapper.toResponse(saved);
    }
}
