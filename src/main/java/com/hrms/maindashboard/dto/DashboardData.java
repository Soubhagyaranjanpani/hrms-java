package com.hrms.maindashboard.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    private int totalEmployees;
    private int newHiresThisMonth;
    private double employeeGrowthPct;
    private int totalTasks;
    private int inProgressTasks;
    private int inReviewTasks;  // ← ADDED
    private int overdueTasks;
    private int completedTasks;
    private double taskCompletionRate;
    private double totalPayrollThisMonth;
    private int pendingPayrollCount;
    private int processedPayrollCount;
    private double avgSalary;
    private double avgPerformanceRating;
    private int outstandingEmployees;
    private int performanceReviewsDone;
    private List<DeptData> depts;
}