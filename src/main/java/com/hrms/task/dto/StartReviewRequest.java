package com.hrms.task.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartReviewRequest {
    private Long employeeId;
    private Double rating;
    private Integer totalGoals;
    private Integer achievedGoals;
    private String improvementPercent;
    private String reviewCycle;          // "Q1 2026"
}