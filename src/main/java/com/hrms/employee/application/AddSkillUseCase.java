package com.hrms.employee.application;

import com.hrms.employee.domain.*;
import com.hrms.employee.dto.SkillRequest;
import com.hrms.employee.infrastructure.*;
import com.hrms.audit.application.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddSkillUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeSkillRepository repo;
    private final AuditLogService audit;

    public String execute(SkillRequest req, String user) {

        Employee emp = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeSkill s = new EmployeeSkill();
        s.setEmployee(emp);
        s.setSkillName(req.getSkillName());
        s.setProficiency(req.getProficiency());

        repo.save(s);

        audit.log("SKILL", s.getId(), "CREATE", user, null, s);

        return "Skill added";
    }
}
