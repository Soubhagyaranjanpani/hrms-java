package com.hrms.task.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.task.application.*;
import com.hrms.task.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final GetMyTasksUseCase getMyTasksUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;

    @PostMapping
    public ApiResponse<TaskResponse> create(
            @RequestBody TaskCreateRequest req,
            Principal p) {

        TaskResponse res = createTaskUseCase.execute(req, p.getName());

        return ResponseUtils.createSuccessResponse(res, new TypeReference<>() {});
    }

    @GetMapping("/my")
    public ApiResponse<List<TaskResponse>> myTasks(Principal p) {

        List<TaskResponse> data = getMyTasksUseCase.execute(p.getName());

        return ResponseUtils.createSuccessResponse(data, new TypeReference<>() {});
    }

    @PostMapping("/status")
    public ApiResponse<String> updateStatus(
            @RequestBody TaskUpdateStatusRequest req) {

        String res = updateTaskStatusUseCase.execute(req.getTaskId(), req.getStatus());

        return ResponseUtils.createSuccessResponse(res, new TypeReference<>() {});
    }
}
