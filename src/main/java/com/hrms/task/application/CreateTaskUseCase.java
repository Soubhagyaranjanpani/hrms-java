package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.task.domain.Task;

import com.hrms.task.domain.TaskPriority;
import com.hrms.task.domain.TaskStatus;
import com.hrms.task.dto.TaskCreateRequest;
import com.hrms.task.dto.TaskResponse;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateTaskUseCase {

    private final TaskRepository taskRepo;
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final BranchRepository branchRepo;

    @Transactional
    public TaskResponse execute(TaskCreateRequest req, String userEmail) {

        Employee assignedTo = employeeRepo.findById(req.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + req.getAssignedToId()));

        Employee assignedBy = employeeRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Department dept = departmentRepo.findById(req.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + req.getDepartmentId()));

        Branch branch = branchRepo.findById(req.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + req.getBranchId()));

        // Validate department belongs to branch
        if (!dept.getBranch().getId().equals(branch.getId())) {
            throw new RuntimeException("Department " + dept.getName() +
                    " does not belong to branch " + branch.getName());
        }

        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());

        // ✅ Use enum conversion
        task.setPriority(TaskPriority.fromString(req.getPriority()));
        task.setStatus(TaskStatus.valueOf(String.valueOf(TaskStatus.PENDING_APPROVAL)));

        task.setProgress(0);
        task.setAssignedTo(assignedTo);
        task.setAssignedBy(assignedBy);
        task.setDepartment(dept);
        task.setBranch(branch);

        task.setStartDate(req.getStartDate());
        task.setDueDate(req.getDueDate());
        task.setEstimatedHours(req.getEstimatedHours());
        task.setEffort(req.getEffort());
        task.setTags(req.getTags());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        if (req.getParentTaskId() != null) {
            Task parent = taskRepo.findById(req.getParentTaskId())
                    .orElseThrow(() -> new RuntimeException("Parent task not found with ID: " + req.getParentTaskId()));

            if (parent.getIsDeleted()) {
                throw new RuntimeException("Cannot create subtask under a deleted parent task");
            }

            if (!parent.getBranch().getId().equals(branch.getId())) {
                throw new RuntimeException("Subtask must belong to the same branch as parent task");
            }

            task.setParentTask(parent);
        }

        Task savedTask = taskRepo.save(task);
        return mapToResponse(savedTask);
    }

    TaskResponse mapToResponse(Task t) {
        TaskResponse r = new TaskResponse();

        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());

        // ✅ Map enums with both display name and code
        if (t.getStatus() != null) {
            r.setStatus(String.valueOf(t.getStatus()));

        }

        if (t.getPriority() != null) {
            r.setPriority(String.valueOf(t.getPriority()));
        }

        r.setProgress(t.getProgress());
        r.setEffort(t.getEffort());
        r.setTags(t.getTags());

        if (t.getAssignedTo() != null) {
            r.setAssignedTo(t.getAssignedTo().getFirstName() + " " + t.getAssignedTo().getLastName());
            r.setAssignedToId(t.getAssignedTo().getId());
        }

        if (t.getAssignedBy() != null) {
            String fullName = t.getAssignedBy().getFirstName() + " " + t.getAssignedBy().getLastName();
            r.setAssignedBy(fullName);
            r.setAssignedById(t.getAssignedBy().getId());
            r.setCreatedByName(fullName);
        }

        if (t.getDepartment() != null) {
            r.setDepartment(t.getDepartment().getName());
            r.setDepartmentId(t.getDepartment().getId());
        }

        if (t.getBranch() != null) {
            r.setBranchId(t.getBranch().getId());
            r.setBranchName(t.getBranch().getName());

        }

        r.setDueDate(t.getDueDate());
        r.setStartDate(t.getStartDate());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());

        r.setEstimatedHours(t.getEstimatedHours());
        r.setActualHours(t.getActualHours());

        if (t.getParentTask() != null) {
            r.setParentTaskId(t.getParentTask().getId());
            r.setParentTaskTitle(t.getParentTask().getTitle());
        }

        if (t.getSubtasks() != null && !t.getSubtasks().isEmpty()) {
            r.setSubtasks(
                    t.getSubtasks()
                            .stream()
                            .filter(sub -> !sub.getIsDeleted())
                            .map(this::mapToResponse)
                            .toList()
            );
        } else {
            r.setSubtasks(List.of());
        }

        return r;
    }
}