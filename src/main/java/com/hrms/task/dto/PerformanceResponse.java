package com.hrms.task.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceResponse {

    private Long id;
    private Long employeeId;
    private String name;             // firstName + lastName
    private String department;
    private Double rating;           // 4.8
    private Integer totalGoals;
    private Integer achievedGoals;
    private String improvementPercent; // "+12%"
    private String status;           // Outstanding, Excellent, Great, Good, Satisfactory
    private String reviewCycle;
}
