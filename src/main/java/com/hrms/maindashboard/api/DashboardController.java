package com.hrms.maindashboard.api;



import com.fasterxml.jackson.core.type.TypeReference;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;

import com.hrms.maindashboard.application.GetDashboardStatsUseCase;
import com.hrms.maindashboard.dto.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetDashboardStatsUseCase statsUseCase;

    // ── GET /api/dashboard/stats ──────────────────────────────────────────────
    // Powers: all 5 stat cards + module snapshots + recent activity + charts
    @GetMapping("/stats")
    public ApiResponse<DashboardStatsResponse> getStats(Principal principal) {
        return ResponseUtils.createSuccessResponse(
                statsUseCase.execute(principal.getName()),
                new TypeReference<>() {}
        );
    }
}
