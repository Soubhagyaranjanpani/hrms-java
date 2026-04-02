package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Department;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.task.domain.Task;
import com.hrms.task.dto.TaskCreateRequest;
import com.hrms.task.dto.TaskResponse;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateTaskUseCase {

    private final TaskRepository taskRepo;
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;

    public TaskResponse execute(TaskCreateRequest req, String userEmail) {

        Employee assignedTo = employeeRepo.findById(req.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee assignedBy = employeeRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Department dept = departmentRepo.findById(req.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setPriority(req.getPriority());
        task.setStatus("TODO");
        task.setAssignedTo(assignedTo);
        task.setAssignedBy(assignedBy);
        task.setDepartment(dept);
        task.setStartDate(req.getStartDate());
        task.setDueDate(req.getDueDate());
        task.setEstimatedHours(req.getEstimatedHours());
        task.setCreatedAt(LocalDateTime.now());

        return map(taskRepo.save(task));
    }

    private TaskResponse map(Task t) {
        TaskResponse r = new TaskResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setPriority(t.getPriority());
        r.setStatus(t.getStatus());
        r.setAssignedTo(t.getAssignedTo().getFirstName());
        r.setAssignedBy(t.getAssignedBy().getFirstName());
        r.setDueDate(t.getDueDate());
        return r;
    }
}
