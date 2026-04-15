package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.task.domain.Task;
import com.hrms.task.dto.TaskResponse;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyTasksUseCase {

    private final TaskRepository taskRepo;
    private final EmployeeRepository employeeRepo;
    private final CreateTaskUseCase createTaskUseCase;

    public List<TaskResponse> execute(String email) {
        Employee emp = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepo.findByAssignedToAndIsDeletedFalse(emp)
                .stream()
                .map(createTaskUseCase::mapToResponse)
                .toList();
    }
}
