package com.hrms.task.application;

import com.hrms.task.domain.Task;
import com.hrms.task.dto.TaskResponse;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllTasksUseCase {

    private final TaskRepository taskRepo;
    private final CreateTaskUseCase createTaskUseCase; // reuse mapper

    public List<TaskResponse> execute() {
        return taskRepo.findByIsDeletedFalseAndParentTaskIsNull()
                .stream()
                .map(createTaskUseCase::mapToResponse)
                .toList();
    }
}
