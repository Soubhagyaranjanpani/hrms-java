package com.hrms.task.application;

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

    @Transactional
    public String execute(TaskUpdateStatusRequest req) {

        Task task = taskRepo.findById(req.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + req.getTaskId()));

        // Convert request status to enum
        TaskStatus newStatus = TaskStatus.fromString(req.getStatus());
        TaskStatus currentStatus = task.getStatus();

        // Validate transition (optional but recommended)
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new RuntimeException(
                    String.format("Invalid status transition from %s to %s",
                            currentStatus.getDisplayName(),
                            newStatus.getDisplayName())
            );
        }

        // Update status
        task.setStatus(newStatus);

        // Update progress if provided
        if (req.getProgress() != null) {
            // Validate progress range
            if (req.getProgress() < 0 || req.getProgress() > 100) {
                throw new RuntimeException("Progress must be between 0 and 100");
            }
            task.setProgress(req.getProgress());
        }

        // Auto-set progress for specific statuses
        if (newStatus == TaskStatus.COMPLETED) {
            task.setProgress(100);
        } else if (newStatus == TaskStatus.PENDING_APPROVAL) {
            task.setProgress(0);
        } else if (newStatus == TaskStatus.REJECTED) {
            // Optional: Reset progress or keep as is
            task.setProgress(0);
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        return String.format("Task status updated to %s", newStatus.getDisplayName());
    }
}