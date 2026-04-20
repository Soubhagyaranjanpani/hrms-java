package com.hrms.payroll.application;

import com.hrms.payroll.dto.PayrollDashboardStats;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPayrollDashboardStatsUseCase {

    private final PayrollRepository repo;

    public PayrollDashboardStats execute(String yearMonth) {
        if (yearMonth == null || yearMonth.isEmpty()) {
            yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        PayrollDashboardStats s = new PayrollDashboardStats();

        // ── current month aggregates ──
        try {
            Object[] agg = repo.aggregateForMonth(yearMonth);

            if (agg != null && agg.length >= 11) {
                s.setTotalNetPayroll(toDouble(agg[0]));
                s.setTotalGrossPayroll(toDouble(agg[1]));
                s.setTotalDeductions(toDouble(agg[2]));
                s.setTotalBasic(toDouble(agg[3]));
                s.setTotalHra(toDouble(agg[4]));
                s.setTotalPF(toDouble(agg[5]));
                s.setTotalTax(toDouble(agg[6]));

                long total = toLong(agg[7]);
                long processed = toLong(agg[8]);
                long pending = toLong(agg[9]);
                long draft = toLong(agg[10]);

                s.setTotalEmployees((int) total);
                s.setProcessedCount((int) processed);
                s.setPendingCount((int) pending);
                s.setDraftCount((int) draft);

                double gross = s.getTotalGrossPayroll() != null ? s.getTotalGrossPayroll() : 0.0;
                double basic = s.getTotalBasic() != null ? s.getTotalBasic() : 0.0;
                s.setTotalAllowances(gross - basic);

                s.setAvgNetSalary(total > 0 ? s.getTotalNetPayroll() / total : 0.0);
            } else {
                setDefaultValues(s);
            }
        } catch (Exception e) {
            setDefaultValues(s);
        }

        // ── 6-month trend ──
        try {
            String from6 = sixMonthsBack(yearMonth);
            List<Object[]> trend = repo.monthlyTrend(from6);
            List<PayrollDashboardStats.MonthTrend> trendList = new ArrayList<>();

            if (trend != null) {
                for (Object[] row : trend) {
                    if (row != null && row.length >= 5) {
                        PayrollDashboardStats.MonthTrend t = new PayrollDashboardStats.MonthTrend();
                        t.setYearMonth(toString(row[0]));
                        t.setLabel(toString(row[1]));
                        t.setNetPayroll(toDouble(row[2]));
                        t.setGrossPayroll(toDouble(row[3]));
                        t.setHeadCount((int) toLong(row[4]));
                        trendList.add(t);
                    }
                }
            }
            s.setTrend(trendList);
        } catch (Exception e) {
            s.setTrend(new ArrayList<>());
        }

        // ── department breakdown ──
        try {
            List<Object[]> depts = repo.deptBreakdown(yearMonth);
            List<PayrollDashboardStats.DeptBreakdown> deptList = new ArrayList<>();

            if (depts != null) {
                for (Object[] row : depts) {
                    if (row != null && row.length >= 3) {
                        PayrollDashboardStats.DeptBreakdown db = new PayrollDashboardStats.DeptBreakdown();
                        db.setDepartment(toString(row[0]));
                        db.setTotalNet(toDouble(row[1]));
                        int count = (int) toLong(row[2]);
                        db.setCount(count);
                        db.setAvgNet(count > 0 ? db.getTotalNet() / count : 0.0);
                        deptList.add(db);
                    }
                }
            }
            s.setDeptBreakdown(deptList);
        } catch (Exception e) {
            s.setDeptBreakdown(new ArrayList<>());
        }

        // ── AI summary ──
        s.setAiSummary(buildAiSummary(s, yearMonth));

        return s;
    }

    private void setDefaultValues(PayrollDashboardStats s) {
        s.setTotalNetPayroll(0.0);
        s.setTotalGrossPayroll(0.0);
        s.setTotalDeductions(0.0);
        s.setTotalBasic(0.0);
        s.setTotalHra(0.0);
        s.setTotalPF(0.0);
        s.setTotalTax(0.0);
        s.setTotalAllowances(0.0);
        s.setTotalEmployees(0);
        s.setProcessedCount(0);
        s.setPendingCount(0);
        s.setDraftCount(0);
        s.setAvgNetSalary(0.0);
    }

    private String buildAiSummary(PayrollDashboardStats s, String ym) {
        double net = s.getTotalNetPayroll() != null ? s.getTotalNetPayroll() : 0.0;
        int emp = s.getTotalEmployees() != null ? s.getTotalEmployees() : 0;
        int pro = s.getProcessedCount() != null ? s.getProcessedCount() : 0;
        int pen = s.getPendingCount() != null ? s.getPendingCount() : 0;

        if (emp == 0) {
            return "No payroll data found for " + toLabel(ym) + ". Generate records to get started.";
        }

        String label = toLabel(ym);
        return String.format(
                "Total payroll outflow for %s is ₹%.1fL across %d employees. %d records processed, %d pending approval.",
                label, net / 100000, emp, pro, pen
        );
    }

    private String sixMonthsBack(String ym) {
        try {
            LocalDate d = LocalDate.parse(ym + "-01").minusMonths(5);
            return d.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            return ym;
        }
    }

    private String toLabel(String ym) {
        try {
            LocalDate d = LocalDate.parse(ym + "-01");
            return d.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        } catch (Exception e) {
            return ym;
        }
    }

    // Safe conversion utilities
    private double toDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        if (obj instanceof BigDecimal) return ((BigDecimal) obj).doubleValue();
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private long toLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Number) return ((Number) obj).longValue();
        if (obj instanceof BigDecimal) return ((BigDecimal) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String toString(Object obj) {
        return obj != null ? obj.toString() : "";
    }
}