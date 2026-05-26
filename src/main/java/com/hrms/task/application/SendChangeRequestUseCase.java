package com.hrms.task.application;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskChangeRequest;
import com.hrms.task.domain.TaskStatus;
import com.hrms.task.dto.TaskChangeRequestDto;
import com.hrms.task.infrastructure.TaskChangeRequestRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SendChangeRequestUseCase {

    private final TaskRepository taskRepo;
    private final TaskChangeRequestRepository changeRequestRepo;

    public String execute(Long taskId, TaskChangeRequestDto dto) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskChangeRequest cr = new TaskChangeRequest();
        cr.setTask(task);
        cr.setReason(dto.getReason());
        cr.setRequestedDeadline(dto.getRequestedDeadline());
        cr.setStatus("PENDING");
        cr.setCreatedAt(LocalDateTime.now());

        changeRequestRepo.save(cr);

        // move task to CHANGE_REQUESTED status
        task.setStatus(TaskStatus.valueOf("CHANGE_REQUESTED"));
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        return "Change request submitted";
    }
}
