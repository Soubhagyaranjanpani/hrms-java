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
    private final EmployeeRepository employeeRepo;
    private final TaskCommentRepository commentRepo;

    public String execute(Long taskId, String comment, String email) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Employee emp = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        TaskComment c = new TaskComment();
        c.setTask(task);
        c.setCreatedBy(emp);
        c.setComment(comment);
        c.setCreatedAt(LocalDateTime.now());

        commentRepo.save(c);

        return "Comment added";
    }
}