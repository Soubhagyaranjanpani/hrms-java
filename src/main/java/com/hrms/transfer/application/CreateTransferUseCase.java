package com.hrms.transfer.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.TransferType;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.master.infrastructure.TransferTypeRepository;
import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.dto.CreateTransferRequest;
import com.hrms.transfer.dto.TransferRecordResponse;
import com.hrms.transfer.infrastructure.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateTransferUseCase {

    private final TransferRepository transferRepo;
    private final EmployeeRepository empRepo;
    private final DepartmentRepository departmentRepo;
    private final BranchRepository branchRepo;
    private final TransferTypeRepository transferTypeRepo;  // ✅ NEW
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final TransferMapper mapper;
    private final PdfTransferLetterGenerator letterGenerator;
    private final TransferDocumentStorageService storageService;

    public TransferRecordResponse execute(CreateTransferRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // ✅ Fetch Transfer Type from master
        TransferType transferType = null;
        if (req.getTransferTypeId() != null) {
            transferType = transferTypeRepo.findById(req.getTransferTypeId())
                    .orElseThrow(() -> new RuntimeException("Transfer Type not found with ID: " + req.getTransferTypeId()));
        }

        TransferRecord r = new TransferRecord();
        r.setEmployee(emp);
        r.setTransferOrderNumber(req.getTransferOrderNumber());

        // ✅ Set TransferType entity (not string)
        r.setTransferType(transferType);

        // ── From department/branch: auto-populated from employee's current record ──
        Department fromDept = emp.getDepartment();
        if (fromDept == null) {
            throw new RuntimeException("Employee has no department on record");
        }
        r.setFromDepartment(fromDept);

        Branch fromBranch = emp.getBranch();
        if (fromBranch == null) {
            throw new RuntimeException("Employee has no branch on record");
        }
        r.setFromBranch(fromBranch);

        // ── To department/branch: selected in the form ──
        Department toDept = departmentRepo.findById(req.getToDepartmentId())
                .orElseThrow(() -> new RuntimeException("To-department not found"));
        r.setToDepartment(toDept);

        Branch toBranch = branchRepo.findById(req.getToBranchId())
                .orElseThrow(() -> new RuntimeException("To-branch not found"));
        r.setToBranch(toBranch);

        // ── Designation snapshot ──
        employeeDesignationRepo.findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId())
                .ifPresent(currentAssignment -> {
                    if (currentAssignment.getDesignation() != null) {
                        r.setDesignationName(currentAssignment.getDesignation().getName());
                    }
                });

        LocalDate date = req.getTransferDate() != null ? req.getTransferDate() : LocalDate.now();
        r.setTransferDate(date);
        r.setEffectiveDate(req.getEffectiveDate() != null ? req.getEffectiveDate() : date);
        r.setTransferReason(req.getTransferReason());
        r.setIsActive(true);

        TransferRecord saved = transferRepo.save(r);

        // Update employee's live department/branch
        emp.setDepartment(toDept);
        emp.setBranch(toBranch);
        empRepo.save(emp);

        // Auto-generate transfer letter
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = transferRepo.save(saved);
        } catch (Exception e) {
            System.err.println("Failed to auto-generate transfer letter: " + e.getMessage());
        }

        return mapper.toResponse(saved);
    }
}