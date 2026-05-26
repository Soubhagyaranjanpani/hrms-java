package com.hrms.task.application;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskChangeRequest;
import com.hrms.task.domain.TaskStatus;
import com.hrms.task.infrastructure.TaskChangeRequestRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApproveChangeRequestUseCase {

    private final TaskRepository taskRepo;
    private final TaskChangeRequestRepository changeRequestRepo;

    public String execute(Long taskId) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskChangeRequest cr = changeRequestRepo
                .findTopByTaskAndStatusOrderByCreatedAtDesc(task, "PENDING")
                .orElseThrow(() -> new RuntimeException("No pending change request found"));

        // apply the new deadline if requested
        if (cr.getRequestedDeadline() != null) {
            task.setDueDate(cr.getRequestedDeadline());
        }

        // move task back to IN_PROGRESS
        task.setStatus(TaskStatus.valueOf("IN_PROGRESS"));
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        // resolve the change request
        cr.setStatus("APPROVED");
        cr.setResolvedAt(LocalDateTime.now());
        changeRequestRepo.save(cr);

        return "Change request approved";
    }
}
