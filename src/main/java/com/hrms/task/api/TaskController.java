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
    private final GetAssignableEmployeesUseCase getAssignableEmployeesUseCase;   // NEW

    // GET /api/tasks  — now role-scoped
    @GetMapping
    public ApiResponse<List<TaskResponse>> getAllTasks(Principal p) {
        return ResponseUtils.createSuccessResponse(
                getAllTasksUseCase.execute(p.getName()),
                new TypeReference<>() {}
        );
    }

    // NEW: GET /api/tasks/assignees?departmentId=
    // "Assign To" dropdown, grouped by department.
    //   Admin/HR -> managers,  Manager -> only his own employees
    @GetMapping("/assignees")
    public ApiResponse<List<DepartmentAssigneesResponse>> assignees(
            @RequestParam(required = false) Long departmentId,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                getAssignableEmployeesUseCase.execute(p.getName(), departmentId),
                new TypeReference<>() {}
        );
    }

    @GetMapping("/my")
    public ApiResponse<List<TaskResponse>> myTasks(Principal p) {
        return ResponseUtils.createSuccessResponse(
                getMyTasksUseCase.execute(p.getName()),
                new TypeReference<>() {}
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                getTaskByIdUseCase.execute(id),
                new TypeReference<>() {}
        );
    }

    @PostMapping
    public ApiResponse<TaskResponse> create(
            @RequestBody TaskCreateRequest req,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                createTaskUseCase.execute(req, p.getName()),
                new TypeReference<>() {}
        );
    }

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

    // POST /api/tasks/status — now needs Principal for role checks
    @PostMapping("/status")
    public ApiResponse<String> updateStatus(
            @RequestBody TaskUpdateStatusRequest req,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                updateTaskStatusUseCase.execute(req, p.getName()),
                new TypeReference<>() {}
        );
    }

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

    @PostMapping("/{id}/change-request")
    public ApiResponse<String> sendChangeRequest(
            @PathVariable Long id,
            @RequestBody TaskChangeRequestDto req) {
        return ResponseUtils.createSuccessResponse(
                sendChangeRequestUseCase.execute(id, req),
                new TypeReference<>() {}
        );
    }

    @PostMapping("/{id}/change-request/approve")
    public ApiResponse<String> approveChangeRequest(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                approveChangeRequestUseCase.execute(id),
                new TypeReference<>() {}
        );
    }

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

    @PutMapping("/{parentId}/subtasks/{subtaskId}")
    public ApiResponse<TaskResponse> updateSubtask(
            @PathVariable Long parentId,
            @PathVariable Long subtaskId,
            @RequestBody UpdateTaskRequest req) {
        return ResponseUtils.createSuccessResponse(
                updateTaskUseCase.execute(subtaskId, req),
                new TypeReference<>() {}
        );
    }

    @GetMapping("/stats/{employeeId}")
    public ResponseEntity<EmployeeTaskStatsResponse> getStats(@PathVariable Long employeeId) {
        return ResponseEntity.ok(getEmployeeTaskStatsUseCase.execute(employeeId));
    }
}