package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskComment;
import com.hrms.task.infrastructure.TaskCommentRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AddTaskCommentUseCase {

    private final TaskRepository taskRepo;
    private final EmployeeRepository empRepo;
    private final TaskCommentRepository repo;

    public String execute(Long taskId, String comment, String email) {

        Task task = taskRepo.findById(taskId).orElseThrow();
        Employee emp = empRepo.findByEmail(email).orElseThrow();

        TaskComment c = new TaskComment();
        c.setTask(task);
        c.setCreatedBy(emp);
        c.setComment(comment);
        c.setCreatedAt(LocalDateTime.now());

        repo.save(c);

        return "Comment added";
    }
}
