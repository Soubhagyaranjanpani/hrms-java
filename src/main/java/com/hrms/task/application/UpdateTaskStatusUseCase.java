package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskStatus;
import com.hrms.task.dto.TaskUpdateStatusRequest;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateTaskStatusUseCase {

    private final TaskRepository taskRepo;
    private final AccessScopeService scope;     // NEW

    @Transactional
    public String execute(TaskUpdateStatusRequest req, String userEmail) {

        Task task = taskRepo.findById(req.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + req.getTaskId()));

        TaskStatus newStatus = TaskStatus.fromString(req.getStatus());
        TaskStatus currentStatus = task.getStatus();

        // NEW ── who may change the status
        Employee user = scope.currentUser(userEmail);
        boolean reviewer = scope.canReviewTask(user, task);
        boolean assignee = scope.isSame(user, task.getAssignedTo());

        if (!reviewer && !assignee) {
            throw scope.forbidden("You are not allowed to change this task");
        }
        if (!reviewer) {
            // assignee may only accept / decline. Completing requires proof + manager approval.
            boolean acceptOrDecline = currentStatus == TaskStatus.PENDING_APPROVAL
                    && (newStatus == TaskStatus.IN_PROGRESS || newStatus == TaskStatus.REJECTED);
            if (!acceptOrDecline) {
                throw scope.forbidden("You can only accept or decline a task. " +
                        "Submit your work with proof to send it for review.");
            }
        }

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new RuntimeException(
                    String.format("Invalid status transition from %s to %s",
                            currentStatus.getDisplayName(), newStatus.getDisplayName()));
        }

        task.setStatus(newStatus);

        if (req.getProgress() != null) {
            if (req.getProgress() < 0 || req.getProgress() > 100) {
                throw new RuntimeException("Progress must be between 0 and 100");
            }
            task.setProgress(req.getProgress());
        }

        if (newStatus == TaskStatus.COMPLETED) {
            task.setProgress(100);
        } else if (newStatus == TaskStatus.PENDING_APPROVAL || newStatus == TaskStatus.REJECTED) {
            task.setProgress(0);
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        return String.format("Task status updated to %s", newStatus.getDisplayName());
    }
}
