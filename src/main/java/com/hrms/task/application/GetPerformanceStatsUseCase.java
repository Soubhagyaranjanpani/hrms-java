package com.hrms.task.application;

import com.hrms.task.dto.PerformanceStatsResponse;
import com.hrms.task.infrastructure.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPerformanceStatsUseCase {

    private final PerformanceReviewRepository repo;

    public PerformanceStatsResponse execute() {

        PerformanceStatsResponse stats = new PerformanceStatsResponse();

        // 1️⃣ Average Rating
        Double avg = repo.findAvgRating();
        stats.setAvgRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);

        // 2️⃣ Total Completed Reviews
        stats.setCompletedReviews((int) repo.findByIsDeletedFalse().size());

        // 3️⃣ Goal Achievement %
        Object[] goalTotals = repo.sumGoals();

        if (goalTotals != null && goalTotals.length >= 2) {

            long achieved = goalTotals[0] != null
                    ? ((Number) goalTotals[0]).longValue()
                    : 0;

            long total = goalTotals[1] != null
                    ? ((Number) goalTotals[1]).longValue()
                    : 0;

            if (total > 0) {
                stats.setGoalAchievementPct((int) ((achieved * 100) / total));
            } else {
                stats.setGoalAchievementPct(0);
            }

        } else {
            stats.setGoalAchievementPct(0);
        }

        // 4️⃣ Outstanding Count (rating >= 4.5)
        Integer outstanding = repo.countByRatingGreaterThanEqual(4.5);
        stats.setOutstandingCount(outstanding != null ? outstanding : 0);

        return stats;
    }
}
