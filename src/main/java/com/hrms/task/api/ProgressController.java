package com.hrms.task.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.task.application.GetEmployeeProgressUseCase;
import com.hrms.task.application.GetTeamProgressUseCase;
import com.hrms.task.dto.EmployeeProgressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final GetEmployeeProgressUseCase getEmployeeProgressUseCase;
    private final GetTeamProgressUseCase getTeamProgressUseCase;

    // Logged-in user's own graph
    @GetMapping("/me")
    public ApiResponse<EmployeeProgressResponse> me(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                getEmployeeProgressUseCase.execute(p.getName(), null, from, to),
                new TypeReference<>() {}
        );
    }

    // One employee (self, his manager, Admin, HR only)
    @GetMapping("/employees/{employeeId}")
    public ApiResponse<EmployeeProgressResponse> employee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                getEmployeeProgressUseCase.execute(p.getName(), employeeId, from, to),
                new TypeReference<>() {}
        );
    }

    // Everybody the viewer may see, optionally one department
    @GetMapping("/team")
    public ApiResponse<List<EmployeeProgressResponse>> team(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                getTeamProgressUseCase.execute(p.getName(), departmentId, from, to),
                new TypeReference<>() {}
        );
    }
}
