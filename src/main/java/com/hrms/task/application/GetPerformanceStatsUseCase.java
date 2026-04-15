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

        // avg rating — stat card 1
        Double avg = repo.findAvgRating();
        stats.setAvgRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);

        // total completed reviews — stat card 2
        stats.setCompletedReviews((int) repo.findByIsDeletedFalse().size());

        // goal achievement % — stat card 3
        Object[] goalTotals = repo.sumGoals();
        if (goalTotals != null && goalTotals[0] != null && goalTotals[1] != null) {
            long achieved = ((Number) goalTotals[0]).longValue();
            long total    = ((Number) goalTotals[1]).longValue();
            stats.setGoalAchievementPct(total > 0 ? (int) ((achieved * 100) / total) : 0);
        } else {
            stats.setGoalAchievementPct(0);
        }

        // outstanding count (rating >= 4.5) — stat card 4
        Integer outstanding = repo.countByRatingGreaterThanEqual(4.5);
        stats.setOutstandingCount(outstanding != null ? outstanding : 0);

        return stats;
    }
}
