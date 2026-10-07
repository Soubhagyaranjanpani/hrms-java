package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeSkillRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class SmartAssignUseCase {

    private final EmployeeSkillRepository skillRepo;
    private final TaskRepository taskRepo;

    public Long assign(String requiredSkill) {

        return skillRepo.findBySkill_Name(requiredSkill)
                .stream()
                .map(s -> s.getEmployee())
                .min(Comparator.comparing(emp ->
                        taskRepo.findByAssignedToAndIsDeletedFalse(emp).size()
                ))
                .map(Employee::getId)
                .orElseThrow(() -> new RuntimeException("No suitable employee found for skill: " + requiredSkill));
    }
}