package com.hrms.task.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.task.application.*;
import com.hrms.task.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final GetAllTasksUseCase getAllTasksUseCase;
    private final GetMyTasksUseCase getMyTasksUseCase;
    private final GetTaskByIdUseCase getTaskByIdUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    private final AddTaskCommentUseCase addTaskCommentUseCase;
    private final SendChangeRequestUseCase sendChangeRequestUseCase;
    private final ApproveChangeRequestUseCase approveChangeRequestUseCase;
    private final RejectChangeRequestUseCase rejectChangeRequestUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final GetEmployeeTaskStatsUseCase getEmployeeTaskStatsUseCase;

    // ──────────────────────────────────────────────────────────
    // GET /api/tasks
    // Used by: TaskDashboard (stats), TaskList (table + kanban)
    // ──────────────────────────────────────────────────────────
    @GetMapping
    public ApiResponse<List<TaskResponse>> getAllTasks() {
        return ResponseUtils.createSuccessResponse(
                getAllTasksUseCase.execute(),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // GET /api/tasks/my
    // Used by: TaskList filter "Assigned to Me"
    // ──────────────────────────────────────────────────────────
    @GetMapping("/my")
    public ApiResponse<List<TaskResponse>> myTasks(Principal p) {
        return ResponseUtils.createSuccessResponse(
                getMyTasksUseCase.execute(p.getName()),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // GET /api/tasks/{id}
    // Used by: TaskDetail (full detail with comments + subtasks + change request)
    // ──────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                getTaskByIdUseCase.execute(id),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // POST /api/tasks
    // Used by: CreateTask form
    // ──────────────────────────────────────────────────────────
    @PostMapping
    public ApiResponse<TaskResponse> create(
            @RequestBody TaskCreateRequest req,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                createTaskUseCase.execute(req, p.getName()),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // POST /api/tasks/{id}/subtasks
    // Used by: "Add Subtask" form inside TaskDetail overview tab
    // ──────────────────────────────────────────────────────────
    @PostMapping("/{id}/subtasks")
    public ApiResponse<TaskResponse> createSubtask(
            @PathVariable Long id,
            @RequestBody TaskCreateRequest req,
            Principal p) {
        req.setParentTaskId(id);
        return ResponseUtils.createSuccessResponse(
                createTaskUseCase.execute(req, p.getName()),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // POST /api/tasks/status
    // Used by: all action banners in TaskDetail
    //   - Approve Task        → IN_PROGRESS
    //   - Reject Task         → REJECTED
    //   - Submit for Review   → IN_REVIEW
    //   - Mark Complete       → COMPLETED
    //   - Approve & Complete  → COMPLETED
    //   - Send Back           → IN_PROGRESS
    // ──────────────────────────────────────────────────────────
    @PostMapping("/status")
    public ApiResponse<String> updateStatus(
            @RequestBody TaskUpdateStatusRequest req) {
        return ResponseUtils.createSuccessResponse(
                updateTaskStatusUseCase.execute(req),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // POST /api/tasks/{id}/comments
    // Used by: Comments tab in TaskDetail
    // ──────────────────────────────────────────────────────────
    @PostMapping("/{id}/comments")
    public ApiResponse<String> addComment(
            @PathVariable Long id,
            @RequestBody TaskCommentRequest req,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                addTaskCommentUseCase.execute(id, req.getComment(), p.getName()),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // POST /api/tasks/{id}/change-request
    // Used by: "Request Change" button in TaskDetail (IN_PROGRESS banner)
    // ──────────────────────────────────────────────────────────
    @PostMapping("/{id}/change-request")
    public ApiResponse<String> sendChangeRequest(
            @PathVariable Long id,
            @RequestBody TaskChangeRequestDto req) {
        return ResponseUtils.createSuccessResponse(
                sendChangeRequestUseCase.execute(id, req),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // POST /api/tasks/{id}/change-request/approve
    // Used by: "Approve Change" button in TaskDetail (CHANGE_REQUESTED banner)
    // ──────────────────────────────────────────────────────────
    @PostMapping("/{id}/change-request/approve")
    public ApiResponse<String> approveChangeRequest(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                approveChangeRequestUseCase.execute(id),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // POST /api/tasks/{id}/change-request/reject
    // Used by: "Reject Change" button in TaskDetail (CHANGE_REQUESTED banner)
    // ──────────────────────────────────────────────────────────
    @PostMapping("/{id}/change-request/reject")
    public ApiResponse<String> rejectChangeRequest(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                rejectChangeRequestUseCase.execute(id),
                new TypeReference<>() {}
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest req) {
        return ResponseUtils.createSuccessResponse(
                updateTaskUseCase.execute(id, req),
                new TypeReference<>() {}
        );
    }

    /**
     * PUT /api/tasks/{parentId}/subtasks/{subtaskId}
     * Edit a subtask — opened from the subtask row edit button in TaskDetail
     * Body: same shape as UpdateTaskRequest
     */
    @PutMapping("/{parentId}/subtasks/{subtaskId}")
    public ApiResponse<TaskResponse> updateSubtask(
            @PathVariable Long parentId,
            @PathVariable Long subtaskId,
            @RequestBody UpdateTaskRequest req) {
        // parentId is available for validation if needed
        return ResponseUtils.createSuccessResponse(
                updateTaskUseCase.execute(subtaskId, req),
                new TypeReference<>() {}
        );
    }


    @GetMapping("/stats/{employeeId}")
    public ResponseEntity<EmployeeTaskStatsResponse> getStats(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(getEmployeeTaskStatsUseCase.execute(employeeId));
    }



}

