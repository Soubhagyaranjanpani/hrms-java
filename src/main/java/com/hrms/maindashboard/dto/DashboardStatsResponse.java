package com.hrms.maindashboard.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class DashboardStatsResponse {

    // ── Greeting ──────────────────────────────────────────────────────────
    private String  loggedInUserName;
    private String  loggedInUserRole;
    private String  currentMonth;        // "April 2025"
    private String  currentDate;         // "Monday, 20 April 2025"

    // ── Core KPI Cards (top row) ──────────────────────────────────────────
    private Integer totalEmployees;
    private Integer activeEmployees;
    private Integer newHiresThisMonth;
    private Double  employeeGrowthPct;   // % vs last month

    private Integer totalTasks;
    private Integer pendingTasks;
    private Integer completedTasksThisMonth;
    private Double  taskCompletionRate;  // %

    private Double  totalPayrollThisMonth;
    private Double  avgSalary;
    private Integer pendingPayrollCount;
    private Integer processedPayrollCount;

    private Double  avgPerformanceRating;
    private Integer outstandingEmployees;
    private Integer performanceReviewsDone;
    private Integer totalReviewsThisQuarter;

    // ── Employee Module Snapshot ──────────────────────────────────────────
    private List<DeptHeadcount>      deptHeadcounts;   // bar chart
    private List<RecentEmployee>     recentEmployees;  // last 5 joined

    // ── Task Module Snapshot ──────────────────────────────────────────────
    private Integer draftTasks;
    private Integer inProgressTasks;
    private Integer inReviewTasks;
    private Integer overdueTasks;
    private List<RecentTask>         recentTasks;      // last 5

    // ── Payroll Module Snapshot ───────────────────────────────────────────
    private List<PayrollMonthTrend>  payrollTrend;     // last 6 months
    private Double                   totalBasicPayroll;
    private Double                   totalDeductions;
    private List<RecentPayslip>      recentPayslips;   // last 3 processed

    // ── Performance Module Snapshot ───────────────────────────────────────
    private List<TopPerformer>       topPerformers;    // top 3
    private List<RatingDistribution> ratingDistribution;

    // ── Activity feed ─────────────────────────────────────────────────────
    private List<ActivityItem>       recentActivity;   // last 10 across all modules

    // ── AI summary ────────────────────────────────────────────────────────
    private String aiSummary;

    // ─── Nested DTOs ─────────────────────────────────────────────────────

    @Getter @Setter
    public static class DeptHeadcount {
        private String department;
        private Integer count;
        private Double  pct;     // % of total
    }

    @Getter @Setter
    public static class RecentEmployee {
        private Long   id;
        private String name;
        private String role;
        private String department;
        private String joinedDate;
        private String employeeCode;
    }

    @Getter @Setter
    public static class RecentTask {
        private Long          id;
        private String        title;
        private String        status;
        private String        priority;
        private String        assignedTo;
        private String        dueDate;
        private Integer       progress;
        private LocalDateTime updatedAt;      // NEW: for activity sorting
    }

    @Getter @Setter
    public static class PayrollMonthTrend {
        private String  yearMonth;    // "2025-03"
        private String  label;        // "Mar 25"
        private Double  netPayroll;
        private Integer headCount;
    }

    @Getter @Setter
    public static class RecentPayslip {
        private String    employeeName;
        private String    payrollMonth;
        private Double    netSalary;
        private String    status;
        private LocalDate paymentDate;   // NEW: for activity sorting
    }

    @Getter @Setter
    public static class TopPerformer {
        private String    employeeName;
        private String    department;
        private Double    rating;
        private String    badge;         // "Outstanding", "Excellent" …
        private Integer   rank;
        private LocalDate reviewDate;    // NEW: for activity sorting
    }

    @Getter @Setter
    public static class RatingDistribution {
        private String  label;   // "Outstanding", "Excellent" …
        private Integer count;
        private Double  pct;
    }

    @Getter @Setter
    public static class ActivityItem {
        private String module;      // "EMPLOYEE" | "TASK" | "PAYROLL" | "PERFORMANCE"
        private String action;      // human-readable description
        private String actor;       // who performed the action
        private String timestamp;   // relative time, e.g. "2 hrs ago", "Just now"
        private String type;        // "create" | "update" | "process" | "rating" | "complete"

        // NEW: Additional fields for better activity tracking
        private Long          referenceId;     // ID of the related entity (employee, task, etc.)
        private LocalDateTime activityTime;    // Actual timestamp for sorting
        private String        icon;            // Optional: icon name for frontend
        private String        color;           // Optional: color code for frontend
    }
}