package com.hrms.attendance.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.attendance.application.*;
import com.hrms.attendance.dto.*;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final CheckInUseCase checkInUseCase;
    private final CheckOutUseCase checkOutUseCase;
    private final GetMyAttendanceUseCase getMyAttendanceUseCase;
    private final GetAttendanceSummaryUseCase summaryUseCase;
    private final GetAttendanceLeaveDashboardUseCase dashboardUseCase;

    @PostMapping("/check-in")
    public ApiResponse<String> checkIn(Principal p) {

        return (
                checkInUseCase.execute(p.getName()));
    }

    @PostMapping("/check-out")
    public ApiResponse<String> checkOut(Principal p) {

        return (
                checkOutUseCase.execute(p.getName())
        );
    }

    @GetMapping("/my")
    public ApiResponse<List<AttendanceResponse>> my(Principal p) {

        return ResponseUtils.createSuccessResponse(
                getMyAttendanceUseCase.execute(p.getName()),
                new TypeReference<>() {}
        );
    }

    @GetMapping("/summary")
    public ApiResponse<AttendanceSummaryResponse> summary(
            @RequestParam int month,
            @RequestParam int year,
            Principal p) {

        return ResponseUtils.createSuccessResponse(
                summaryUseCase.execute(p.getName(), month, year),
                new TypeReference<>() {}
        );
    }
    @GetMapping("/dashboard")
    public ApiResponse<List<AttendanceLeaveDashboardResponse>> dashboard(
            @RequestParam int month,
            @RequestParam int year,
            Principal p) {

        return ResponseUtils.createSuccessResponse(
                dashboardUseCase.execute(p.getName(), month, year),
                new TypeReference<>() {}
        );
    }
}
