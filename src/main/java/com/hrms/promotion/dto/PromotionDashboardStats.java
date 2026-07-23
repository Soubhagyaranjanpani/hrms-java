package com.hrms.promotion.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class PromotionDashboardStats {

    private Integer totalPromotions;
    private Integer activeCount;
    private Integer inactiveCount;
    private Double  avgIncrementAmount;
    private Double  avgIncrementPercent;

    private List<YearTrend> trend;
    private List<DeptBreakdown> deptBreakdown;

    private String aiSummary;

    @Getter @Setter
    public static class YearTrend {
        private String year;
        private Integer count;
        private Double avgIncrementPercent;
    }

    @Getter @Setter
    public static class DeptBreakdown {
        private String department;
        private Integer count;
        private Double avgIncrementAmount;
    }
}