package com.hrms.promotion.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.domain.EmployeeGrade;
import com.hrms.employee.domain.PromotionType;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeGradeRepository;
import com.hrms.employee.infrastructure.PromotionTypeRepository;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Designation;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.master.infrastructure.DesignationRepository;
import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.infrastructure.PayrollRepository;
import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.dto.CreatePromotionRequest;
import com.hrms.promotion.dto.PromotionRecordResponse;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePromotionUseCase {

    private final PromotionRepository promoRepo;
    private final EmployeeRepository empRepo;
    private final DesignationRepository designationRepo;
    private final DepartmentRepository departmentRepo;
    private final BranchRepository branchRepo;
    private final EmployeeGradeRepository gradeRepo;
    private final PromotionTypeRepository promotionTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final PayrollRepository payrollRepo;
    private final PromotionMapper mapper;
    private final PdfPromotionLetterGenerator letterGenerator;
    private final PromotionDocumentStorageService storageService;
    private final AuditService auditService;

    public PromotionRecordResponse execute(CreatePromotionRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        PromotionRecord r = new PromotionRecord();
        r.setEmployee(emp);
        r.setPromotionOrderNumber(req.getPromotionOrderNumber());

        PromotionType type = promotionTypeRepo.findById(req.getPromotionTypeId())
                .orElseThrow(() -> new RuntimeException("Promotion type not found"));
        r.setPromotionType(type);

        // ── Designation ──
        Designation oldDesig;
        if (req.getOldDesignationId() != null) {
            oldDesig = designationRepo.findById(req.getOldDesignationId())
                    .orElseThrow(() -> new RuntimeException("Old designation not found"));
        } else {
            EmployeeDesignation currentAssignment = employeeDesignationRepo
                    .findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee has no active designation on record — old designation must be supplied explicitly"));
            oldDesig = currentAssignment.getDesignation();
        }
        r.setOldDesignation(oldDesig);

        Designation newDesig = designationRepo.findById(req.getNewDesignationId())
                .orElseThrow(() -> new RuntimeException("New designation not found"));
        r.setNewDesignation(newDesig);

        // ── Department ──
        Department oldDept;
        if (req.getOldDepartmentId() != null) {
            oldDept = departmentRepo.findById(req.getOldDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Old department not found"));
        } else {
            oldDept = emp.getDepartment();
            if (oldDept == null) {
                throw new RuntimeException(
                        "Employee has no department on record — old department must be supplied explicitly");
            }
        }
        r.setOldDepartment(oldDept);

        Department newDept = departmentRepo.findById(req.getNewDepartmentId())
                .orElseThrow(() -> new RuntimeException("New department not found"));
        r.setNewDepartment(newDept);

        // ── Branch ──
        Branch oldBranch;
        if (req.getOldBranchId() != null) {
            oldBranch = branchRepo.findById(req.getOldBranchId())
                    .orElseThrow(() -> new RuntimeException("Old branch not found"));
        } else if (emp.getBranch() != null) {
            oldBranch = emp.getBranch();
        } else if (oldDept.getBranch() != null) {
            oldBranch = oldDept.getBranch();
        } else {
            throw new RuntimeException(
                    "Could not determine old branch — employee has no branch on record and old department has no branch; supply oldBranchId explicitly");
        }
        r.setOldBranch(oldBranch);

        Branch newBranch;
        if (req.getNewBranchId() != null) {
            newBranch = branchRepo.findById(req.getNewBranchId())
                    .orElseThrow(() -> new RuntimeException("New branch not found"));
        } else if (newDept.getBranch() != null) {
            newBranch = newDept.getBranch();
        } else {
            throw new RuntimeException(
                    "Could not determine new branch — new department has no branch; supply newBranchId explicitly");
        }
        r.setNewBranch(newBranch);

        // ── Grade ──
        EmployeeGrade prevGrade;
        if (req.getPreviousGradeId() != null) {
            prevGrade = gradeRepo.findById(req.getPreviousGradeId())
                    .orElseThrow(() -> new RuntimeException("Previous grade not found"));
        } else {
            prevGrade = emp.getGrade();
        }
        r.setPreviousGrade(prevGrade);

        EmployeeGrade newGrade = gradeRepo.findById(req.getNewGradeId())
                .orElseThrow(() -> new RuntimeException("New grade not found"));
        r.setNewGrade(newGrade);

        // ── Salary ──
        Double oldSalary = req.getOldSalary();
        if (oldSalary == null) {
            List<PayrollRecord> history = payrollRepo
                    .findByEmployee_IdAndIsDeletedFalseOrderByYearMonthDesc(emp.getId());
            oldSalary = history.isEmpty() ? 0.0 : history.get(0).getGrossEarnings();
        }
        r.setOldSalary(oldSalary);
        r.setNewSalary(req.getNewSalary() != null ? req.getNewSalary() : 0.0);

        // ── Dates ──
        LocalDate date = req.getPromotionDate() != null ? req.getPromotionDate() : LocalDate.now();
        r.setPromotionDate(date);
        r.setPromotionYear(String.valueOf(date.getYear()));
        r.setEffectiveDate(req.getEffectiveDate() != null ? req.getEffectiveDate() : date);

        // ── Authority ──
        EmployeeDesignation authority = employeeDesignationRepo.findById(req.getPromotionAuthorityId())
                .orElseThrow(() -> new RuntimeException("Promotion authority not found"));
        r.setPromotionAuthority(authority);

        r.setRemarks(req.getRemarks());
        r.setIsActive(true);

        r.compute();

        PromotionRecord saved = promoRepo.save(r);

        // Update the employee's live department/grade/branch
        emp.setDepartment(newDept);
        emp.setGrade(newGrade);
        emp.setBranch(newBranch);
        empRepo.save(emp);

        // Auto-generate the promotion letter
        boolean letterGenerated = false;
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = promoRepo.save(saved);
            letterGenerated = true;
        } catch (Exception e) {
            System.err.println("Failed to auto-generate promotion letter for id " + saved.getId() + ": " + e.getMessage());
        }

        // ── AUDIT LOGGING ──

        // 1. Main promotion creation audit
        auditService.log(
                AuditModule.PROMOTION,
                AuditAction.CREATE,
                "Promotion created for " + emp.getFullName() +
                        " (" + emp.getEmployeeCode() + ")",
                emp,
                "Promotion Record",
                null,
                "Order: " + saved.getPromotionOrderNumber() +
                        ", Type: " + type.toString(),
                "Promotion from " + oldDesig.getName() + " to " + newDesig.getName() +
                        (req.getRemarks() != null ? ", Remarks: " + req.getRemarks() : ""),
                saved.getId()
        );

        // 2. Designation change audit
        auditService.log(
                AuditModule.PROMOTION,
                AuditAction.UPDATE,
                "Designation changed for " + emp.getFullName(),
                emp,
                "Designation",
                oldDesig.getName(),
                newDesig.getName(),
                "Promotion: " + saved.getPromotionOrderNumber(),
                saved.getId()
        );

        // 3. Department change audit
        auditService.log(
                AuditModule.PROMOTION,
                AuditAction.UPDATE,
                "Department changed for " + emp.getFullName(),
                emp,
                "Department",
                oldDept.getName(),
                newDept.getName(),
                "Promotion: " + saved.getPromotionOrderNumber(),
                saved.getId()
        );

        // 4. Branch change audit
        auditService.log(
                AuditModule.PROMOTION,
                AuditAction.UPDATE,
                "Branch changed for " + emp.getFullName(),
                emp,
                "Branch",
                oldBranch.getName(),
                newBranch.getName(),
                "Promotion: " + saved.getPromotionOrderNumber(),
                saved.getId()
        );

        // 5. Grade change audit
        if (prevGrade != null || newGrade != null) {
            auditService.log(
                    AuditModule.PROMOTION,
                    AuditAction.UPDATE,
                    "Grade changed for " + emp.getFullName(),
                    emp,
                    "Grade",
                    prevGrade != null ? prevGrade.toString() : "None",
                    newGrade != null ? newGrade.toString() : "None",
                    "Promotion: " + saved.getPromotionOrderNumber(),
                    saved.getId()
            );
        }

        // 6. Salary change audit
        auditService.log(
                AuditModule.PROMOTION,
                AuditAction.UPDATE,
                "Salary changed for " + emp.getFullName(),
                emp,
                "Salary",
                String.valueOf(oldSalary),
                String.valueOf(saved.getNewSalary()),
                "Promotion: " + saved.getPromotionOrderNumber(),
                saved.getId()
        );

        // 7. Document generation audit (if successful)
        if (letterGenerated) {
            auditService.log(
                    AuditModule.DOCUMENTS,
                    AuditAction.UPLOAD,
                    "Promotion letter generated: " + saved.getDocumentName(),
                    emp,
                    "Document",
                    null,
                    saved.getDocumentName(),
                    "Auto-generated promotion letter",
                    saved.getId()
            );
        }

        return mapper.toResponse(saved);
    }
}