package com.hrms.task.application;



import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskChangeRequest;
import com.hrms.task.domain.TaskComment;
import com.hrms.task.dto.ChangeRequestResponse;
import com.hrms.task.dto.CommentResponse;
import com.hrms.task.dto.TaskResponse;

import com.hrms.task.infrastructure.TaskChangeRequestRepository;
import com.hrms.task.infrastructure.TaskCommentRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetTaskByIdUseCase {

    private final TaskRepository taskRepo;
    private final TaskCommentRepository commentRepo;
    private final TaskChangeRequestRepository changeRequestRepo;
    private final CreateTaskUseCase createTaskUseCase; // reuse base mapper

    public TaskResponse execute(Long taskId) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // base fields
        TaskResponse r = createTaskUseCase.mapToResponse(task);

        // comments
        List<CommentResponse> comments = commentRepo
                .findByTaskOrderByCreatedAtAsc(task)
                .stream()
                .map(this::mapComment)
                .toList();
        r.setComments(comments);

        // subtasks (children)
        List<TaskResponse> subtasks = taskRepo
                .findByParentTaskAndIsDeletedFalse(task)
                .stream()
                .map(createTaskUseCase::mapToResponse)
                .toList();
        r.setSubtasks(subtasks);

        // active change request (PENDING only)
        Optional<TaskChangeRequest> cr = changeRequestRepo
                .findTopByTaskAndStatusOrderByCreatedAtDesc(task, "PENDING");
        cr.ifPresent(c -> r.setChangeRequest(mapChangeRequest(c)));

        return r;
    }

    private CommentResponse mapComment(TaskComment c) {
        CommentResponse cr = new CommentResponse();
        cr.setId(c.getId());
        cr.setAuthor(c.getCreatedBy().getFirstName() + " " + c.getCreatedBy().getLastName());
        cr.setComment(c.getComment());
        cr.setCreatedAt(c.getCreatedAt());
        return cr;
    }

    private ChangeRequestResponse mapChangeRequest(TaskChangeRequest c) {
        ChangeRequestResponse res = new ChangeRequestResponse();
        res.setId(c.getId());
        res.setReason(c.getReason());
        res.setRequestedDeadline(c.getRequestedDeadline());
        res.setStatus(c.getStatus());
        res.setCreatedAt(c.getCreatedAt());
        return res;
    }
}
