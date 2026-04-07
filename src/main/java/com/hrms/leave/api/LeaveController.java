package com.hrms.leave.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.application.*;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final ApplyLeaveUseCase applyLeaveUseCase;
    private final ApproveLeaveUseCase approveLeaveUseCase;
    private final RejectLeaveUseCase rejectLeaveUseCase;
    private final GetTeamLeavesUseCase getTeamLeavesUseCase;
    private final GetMyLeavesUseCase getMyLeavesUseCase;
    private final GetLeaveCalendarUseCase getLeaveCalendarUseCase;
    private final GetAllLeavesUseCase getAllLeavesUseCase;
    private final GetLeaveDashboardUseCase getLeaveDashboardUseCase;

    // 🔥 APPLY LEAVE
    @Operation(summary = "Apply for leave")
    @PostMapping("/apply")
    public ApiResponse<String> applyLeave(
            @RequestBody LeaveApplyRequest request,
            Principal principal) {

        String result = applyLeaveUseCase.execute(request, principal);

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }
    // 🔥 APPROVE LEAVE
    @Operation(summary = "Approve leave")
    @PostMapping("/approve")
    public ApiResponse<String> approve(
            @RequestBody LeaveApprovalRequest request,
            Principal principal) {

        String result = approveLeaveUseCase.execute(request, principal);

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }

    // 🔥 REJECT LEAVE
    @Operation(summary = "Reject leave")
    @PostMapping("/reject")
    public ApiResponse<String> reject(
            @RequestBody LeaveApprovalRequest request,
            Principal principal) {

        String result = rejectLeaveUseCase.execute(request, principal.getName());

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }

    // 🔥 MY LEAVES
    @Operation(summary = "Get my leaves")
    @GetMapping("/my")
    public ApiResponse<List<LeaveResponse>> getMyLeaves(Principal principal) {

        List<LeaveResponse> data = getMyLeavesUseCase.execute(principal);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<>() {}
        );
    }

    // 🔥 TEAM LEAVES
    @Operation(summary = "Get team leaves")
    @GetMapping("/team")
    public ApiResponse<List<LeaveResponse>> getTeamLeaves(Principal principal) {

        List<LeaveResponse> data = getTeamLeavesUseCase.execute(principal);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<>() {}
        );
    }

    // 🔥 LEAVE CALENDAR
    @Operation(summary = "Get leave calendar")
    @GetMapping("/calendar")
    public ApiResponse<List<LeaveCalendarResponse>> getCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<LeaveCalendarResponse> data =
                getLeaveCalendarUseCase.execute(startDate, endDate);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<>() {}
        );
    }
    @PreAuthorize("hasRole('HR')")
    @GetMapping("/all")
    public ApiResponse<List<Leave>> getAllLeaves(
            @RequestParam(required = false) LeaveStatus status) {

        return getAllLeavesUseCase.execute(status);
    }

    @GetMapping("/dashboard")
    public ApiResponse<LeaveDashboardResponse> getDashboard(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseUtils.createSuccessResponse(
                getLeaveDashboardUseCase.execute(principal, page, size),
                new TypeReference<>() {}
        );
    }

}
