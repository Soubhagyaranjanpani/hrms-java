package com.hrms.employee.application;

import com.hrms.employee.domain.*;
import com.hrms.employee.dto.TrainingRequest;
import com.hrms.employee.infrastructure.*;
import com.hrms.audit.application.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddTrainingUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeTrainingRepository repo;
    private final AuditLogService audit;

    public String execute(TrainingRequest req, String user) {

        Employee emp = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeTraining t = new EmployeeTraining();
        t.setEmployee(emp);
        t.setTrainingName(req.getTrainingName());
        t.setCompletedOn(req.getCompletedOn());

        repo.save(t);

        audit.log("TRAINING", t.getId(), "CREATE", user, null, t);

        return "Training added";
    }
}
