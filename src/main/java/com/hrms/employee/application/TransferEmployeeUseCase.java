package com.hrms.employee.application;

import com.hrms.audit.application.AuditLogService;
import com.hrms.employee.domain.*;
import com.hrms.employee.dto.TransferRequest;
import com.hrms.employee.infrastructure.*;
import com.hrms.master.domain.*;
import com.hrms.master.infrastructure.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferEmployeeUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeServiceHistoryRepository historyRepo;
    private final DepartmentRepository departmentRepo;
    private final BranchRepository branchRepo;
    private final AuditLogService audit;

    public String execute(TransferRequest req, String user) {

        Employee emp = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Department newDept = departmentRepo.findById(req.getNewDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Branch newBranch = branchRepo.findById(req.getNewBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // 🔥 Close previous history
        historyRepo.findByEmployee_Id(emp.getId())
                .stream()
                .reduce((first, second) -> second)
                .ifPresent(last -> {
                    last.setToDate(req.getEffectiveDate().minusDays(1));
                    historyRepo.save(last);
                });

        // 🔥 Update employee master
        emp.setDepartment(newDept);
        emp.setBranch(newBranch);
        employeeRepo.save(emp);

        // 🔥 Create new history
        EmployeeServiceHistory history = new EmployeeServiceHistory();
        history.setEmployee(emp);
        history.setDesignation(null); // same designation
        history.setDepartment(newDept);
        history.setDepartmentNameSnapshot(newDept.getName());
        history.setFromDate(req.getEffectiveDate());
        history.setOrderReference(req.getOrderReference());

        historyRepo.save(history);

        audit.log("EMPLOYEE", emp.getId(), "TRANSFER", user, null, req);

        return "Employee transferred successfully";
    }
}
