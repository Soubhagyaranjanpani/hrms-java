package com.hrms.task.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceStatsResponse {

    private Double avgRating;            // 4.3
    private Integer completedReviews;    // 124
    private Integer goalAchievementPct;  // 87
    private Integer outstandingCount;    // employees with rating >= 4.5
}
