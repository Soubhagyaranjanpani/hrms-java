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

    public List<TaskResponse> execute(String email) {

        Employee emp = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepo.findByAssignedToAndIsDeletedFalse(emp)
                .stream().map(this::map).toList();
    }

    private TaskResponse map(Task t) {
        TaskResponse r = new TaskResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setStatus(t.getStatus());
        r.setPriority(t.getPriority());
        r.setAssignedTo(t.getAssignedTo().getFirstName());
        r.setAssignedBy(t.getAssignedBy().getFirstName());
        r.setDueDate(t.getDueDate());
        return r;
    }
}
