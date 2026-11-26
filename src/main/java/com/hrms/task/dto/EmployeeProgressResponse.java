package com.hrms.task.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeProgressResponse {
    private Long employeeId;
    private String employeeName;
    private String role;
    private Long departmentId;
    private String department;

    private LocalDate from;
    private LocalDate to;

    private Long totalSubmissions;
    private Long approvedSubmissions;
    private Long rejectedSubmissions;
    private Long lateSubmissions;
    private Integer activeDays;              // days with at least 1 submission
    private Double avgDailySubmissions;      // totalSubmissions / days in range
    private Double avgDailyHours;

    private EmployeeTaskStatsResponse taskStats;
    private List<DailyProgressPoint> daily;  // one point per day (zeros included) -> graph
}
