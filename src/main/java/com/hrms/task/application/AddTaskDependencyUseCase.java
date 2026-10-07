package com.hrms.task.application;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskDependency;
import com.hrms.task.infrastructure.TaskDependencyRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddTaskDependencyUseCase {

    private final TaskRepository taskRepo;
    private final TaskDependencyRepository repo;

    public String execute(Long taskId, Long dependsOnId) {

        Task task = taskRepo.findById(taskId).orElseThrow();
        Task dep = taskRepo.findById(dependsOnId).orElseThrow();

        TaskDependency d = new TaskDependency();
        d.setTask(task);
        d.setDependsOnTask(dep);

        repo.save(d);

        return "Dependency added";
    }
}
