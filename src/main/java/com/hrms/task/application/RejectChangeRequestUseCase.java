package com.hrms.task.application;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskChangeRequest;
import com.hrms.task.domain.TaskStatus;
import com.hrms.task.infrastructure.TaskChangeRequestRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RejectChangeRequestUseCase {

    private final TaskRepository taskRepo;
    private final TaskChangeRequestRepository changeRequestRepo;

    @Transactional
    public String execute(Long taskId) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        TaskChangeRequest cr = changeRequestRepo
                .findTopByTaskAndStatusOrderByCreatedAtDesc(task, "PENDING")
                .orElseThrow(() -> new RuntimeException("No pending change request found for task ID: " + taskId));

        // Validate task is in correct status for rejecting change request
        if (task.getStatus() != TaskStatus.CHANGE_REQUESTED) {
            throw new RuntimeException(
                    String.format("Task is not in CHANGE_REQUESTED status. Current status: %s",
                            task.getStatus())
            );
        }

        // Move task back to IN_PROGRESS (reject the change, continue as-is)
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        // Mark change request as rejected
        cr.setStatus("REJECTED");
        cr.setResolvedAt(LocalDateTime.now());
        changeRequestRepo.save(cr);

        return String.format("Change request rejected. Task '%s' moved back to In Progress",
                task.getTitle());
    }
}