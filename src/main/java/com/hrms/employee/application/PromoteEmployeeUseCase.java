package com.hrms.employee.application;

import com.hrms.audit.application.AuditLogService;
import com.hrms.employee.domain.*;
import com.hrms.employee.dto.PromotionRequest;
import com.hrms.employee.infrastructure.*;
import com.hrms.master.domain.Designation;
import com.hrms.master.infrastructure.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromoteEmployeeUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeServiceHistoryRepository historyRepo;
    private final AuditLogService audit;
    private final DesignationRepository designationRepo;

    public String execute(PromotionRequest req, String user) {

        Employee emp = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 🔥 Close previous history
        historyRepo.findByEmployee_Id(emp.getId())
                .stream()
                .reduce((first, second) -> second) // get last record
                .ifPresent(last -> {
                    last.setToDate(req.getEffectiveDate().minusDays(1));
                    historyRepo.save(last);
                });

        // 🔥 Create new history
        EmployeeServiceHistory history = new EmployeeServiceHistory();
        history.setEmployee(emp);
        Designation designation = designationRepo.findById(req.getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        history.setDesignation(designation);
        history.setDepartment(emp.getDepartment());
        history.setDepartmentNameSnapshot(
                emp.getDepartment() != null ? emp.getDepartment().getName() : null
        );
        history.setFromDate(req.getEffectiveDate());
        history.setOrderReference(req.getOrderReference());

        historyRepo.save(history);

        // (Optional) You can store current designation in Employee if needed

        audit.log("EMPLOYEE", emp.getId(), "PROMOTION", user, null, req);

        return "Employee promoted successfully";
    }
}
