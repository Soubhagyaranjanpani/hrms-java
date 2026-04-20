package com.hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PayrollDashboardStats {

    // Stat cards
    private Double  totalNetPayroll;
    private Double  totalGrossPayroll;
    private Integer totalEmployees;
    private Integer processedCount;
    private Integer pendingCount;
    private Integer draftCount;
    private Double  avgNetSalary;

    // Distribution
    private Double totalBasic;
    private Double totalHra;
    private Double totalAllowances;
    private Double totalDeductions;
    private Double totalPF;
    private Double totalTax;

    // Month-over-month trend (last 6 months)
    private List<MonthTrend> trend;

    // Department-wise payroll
    private List<DeptBreakdown> deptBreakdown;

    // AI summary
    private String aiSummary;

    @Getter @Setter
    public static class MonthTrend {
        private String yearMonth;
        private String label;
        private Double netPayroll;
        private Double grossPayroll;
        private Integer headCount;
    }

    @Getter @Setter
    public static class DeptBreakdown {
        private String department;
        private Double totalNet;
        private Integer count;
        private Double avgNet;
    }
}