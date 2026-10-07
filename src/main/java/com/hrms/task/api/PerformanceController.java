package com.hrms.performance.api;

import com.fasterxml.jackson.core.type.TypeReference;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;

import com.hrms.task.application.GetPerformanceListUseCase;
import com.hrms.task.application.GetPerformanceStatsUseCase;
import com.hrms.task.application.GetTopPerformersUseCase;
import com.hrms.task.application.StartReviewUseCase;
import com.hrms.task.dto.PerformanceResponse;
import com.hrms.task.dto.PerformanceStatsResponse;
import com.hrms.task.dto.StartReviewRequest;
import com.hrms.task.dto.TopPerformerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final GetPerformanceListUseCase getPerformanceListUseCase;
    private final GetPerformanceStatsUseCase getPerformanceStatsUseCase;
    private final GetTopPerformersUseCase getTopPerformersUseCase;
    private final StartReviewUseCase startReviewUseCase;

    // ──────────────────────────────────────────────────────────
    // GET /api/performance
    // Used by: Performance.jsx — main table (all employee rows)
    // Returns: id, name, department, rating, goals, progress,
    //          improvement, status, reviewCycle
    // ──────────────────────────────────────────────────────────
    @GetMapping
    public ApiResponse<List<PerformanceResponse>> getAll() {
        return ResponseUtils.createSuccessResponse(
                getPerformanceListUseCase.execute(),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // GET /api/performance/stats
    // Used by: Performance.jsx — 4 stat cards at the top
    //   Card 1: Avg Rating        → avgRating
    //   Card 2: Completed Reviews → completedReviews
    //   Card 3: Goal Achievement  → goalAchievementPct
    //   Card 4: Outstanding       → outstandingCount
    // ──────────────────────────────────────────────────────────
    @GetMapping("/stats")
    public ApiResponse<PerformanceStatsResponse> getStats() {
        return ResponseUtils.createSuccessResponse(
                getPerformanceStatsUseCase.execute(),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // GET /api/performance/top
    // Used by: Performance.jsx — Top Performers section (3 cards)
    // Returns: name, department, rating, improvementPercent
    // ──────────────────────────────────────────────────────────
    @GetMapping("/top")
    public ApiResponse<List<TopPerformerResponse>> getTopPerformers() {
        return ResponseUtils.createSuccessResponse(
                getTopPerformersUseCase.execute(),
                new TypeReference<>() {}
        );
    }

    // ──────────────────────────────────────────────────────────
    // POST /api/performance
    // Used by: "Start Review Cycle" button in Performance.jsx
    // Body: { employeeId, rating, totalGoals, achievedGoals,
    //         improvementPercent, reviewCycle }
    // ──────────────────────────────────────────────────────────
    @PostMapping
    public ApiResponse<PerformanceResponse> startReview(
            @RequestBody StartReviewRequest req) {
        return ResponseUtils.createSuccessResponse(
                startReviewUseCase.execute(req),
                new TypeReference<>() {}
        );
    }
}
