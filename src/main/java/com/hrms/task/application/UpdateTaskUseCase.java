package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Department;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskPriority;
import com.hrms.task.dto.TaskResponse;
import com.hrms.task.dto.UpdateTaskRequest;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateTaskUseCase {

    private final TaskRepository     taskRepo;
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final CreateTaskUseCase  createTaskUseCase; // reuse mapper

    /**
     * Works for BOTH main tasks and subtasks.
     * The only difference is subtasks have a non-null parentTask — the
     * caller does not need to worry about that distinction.
     */
    public TaskResponse execute(Long taskId, UpdateTaskRequest req) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // ── only update fields that were actually sent ──
        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            task.setTitle(req.getTitle().trim());
        }

        if (req.getDescription() != null) {
            task.setDescription(req.getDescription().trim());
        }

        if (req.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(req.getPriority().toUpperCase()));
        }

        if (req.getAssignedToId() != null) {
            Employee assignedTo = employeeRepo.findById(req.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + req.getAssignedToId()));
            task.setAssignedTo(assignedTo);
        }

        if (req.getDepartmentId() != null) {
            Department dept = departmentRepo.findById(req.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + req.getDepartmentId()));
            task.setDepartment(dept);
        }

        if (req.getDueDate() != null) {
            task.setDueDate(req.getDueDate());
        }

        if (req.getEffort() != null) {
            task.setEffort(req.getEffort());
        }

        if (req.getTags() != null) {
            task.setTags(req.getTags());
        }

        if (req.getProgress() != null) {
            task.setProgress(req.getProgress());
        }

        task.setUpdatedAt(LocalDateTime.now());

        return createTaskUseCase.mapToResponse(taskRepo.save(task));
    }
}