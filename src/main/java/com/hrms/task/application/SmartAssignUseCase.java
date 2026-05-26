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

    /**
     * Returns the employee ID of the best match for the required skill,
     * picking the one with the fewest active tasks (load balancing).
     */
    public Long assign(String requiredSkill) {

        return skillRepo.findBySkillName(requiredSkill)
                .stream()
                .map(s -> s.getEmployee())
                .min(Comparator.comparing(emp ->
                        taskRepo.findByAssignedToAndIsDeletedFalse(emp).size()
                ))
                .map(Employee::getId)
                .orElseThrow(() -> new RuntimeException("No suitable employee found for skill: " + requiredSkill));
    }
}