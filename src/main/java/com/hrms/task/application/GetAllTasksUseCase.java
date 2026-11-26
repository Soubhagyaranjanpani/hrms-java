package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
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
    private final AccessScopeService scope;

    /** Role-scoped: Admin/HR see all, Manager sees own + team's, Employee sees own. */
    public List<TaskResponse> execute(String email) {
        Employee viewer = scope.currentUser(email);
        return taskRepo.findByIsDeletedFalseAndParentTaskIsNull()
                .stream()
                .filter(t -> scope.canViewTask(viewer, t))
                .map(createTaskUseCase::mapToResponse)
                .toList();
    }
}
