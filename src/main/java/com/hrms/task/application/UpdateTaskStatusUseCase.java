package com.hrms.task.application;

import com.hrms.task.domain.Task;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTaskStatusUseCase {

    private final TaskRepository taskRepo;

    public String execute(Long taskId, String status) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);
        taskRepo.save(task);

        return "Task status updated";
    }
}
