package com.hrms.employee.application;

import com.hrms.employee.domain.*;
import com.hrms.employee.dto.QualificationRequest;
import com.hrms.employee.infrastructure.*;
import com.hrms.audit.application.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddQualificationUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeQualificationRepository repo;
    private final AuditLogService audit;

    public String execute(QualificationRequest req, String user) {

        Employee emp = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeQualification q = new EmployeeQualification();
        q.setEmployee(emp);
        q.setDegree(req.getDegree());
        q.setInstitution(req.getInstitution());
        q.setYear(req.getYear());

        repo.save(q);

        audit.log("QUALIFICATION", q.getId(), "CREATE", user, null, q);

        return "Qualification added";
    }
}
