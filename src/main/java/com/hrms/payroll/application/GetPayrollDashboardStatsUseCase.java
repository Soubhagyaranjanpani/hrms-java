package com.hrms.payroll.application;

import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.dto.PayrollDashboardStats;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPayrollDashboardStatsUseCase {

    private final PayrollRepository repo;

    public PayrollDashboardStats execute(String yearMonth) {
        if (yearMonth == null || yearMonth.isEmpty()) {
            yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        log.info("Fetching dashboard stats for month: {}", yearMonth);

        PayrollDashboardStats s = new PayrollDashboardStats();

        // Fetch all records for the month - this is reliable
        List<PayrollRecord> records = repo.findByYearMonthAndIsDeletedFalse(yearMonth);

        if (records.isEmpty()) {
            setDefaultValues(s);
            s.setAiSummary("No payroll data found for " + toLabel(yearMonth) + ". Generate records to get started.");
            s.setTrend(getMonthlyTrend(yearMonth));
            s.setDeptBreakdown(new ArrayList<>());
            return s;
        }

        // Calculate all aggregates from records
        calculateAggregates(s, records, yearMonth);

        // Get trend data
        s.setTrend(getMonthlyTrend(yearMonth));

        // Get department breakdown
        s.setDeptBreakdown(calculateDeptBreakdown(records));

        // Generate AI summary
        s.setAiSummary(buildAiSummary(s, yearMonth));

        return s;
    }

    private void calculateAggregates(PayrollDashboardStats s, List<PayrollRecord> records, String yearMonth) {
        // Basic counts
        long totalEmployees = records.size();
        long processedCount = records.stream().filter(r -> "PROCESSED".equals(r.getStatus())).count();
        long pendingCount = records.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long draftCount = records.stream().filter(r -> "DRAFT".equals(r.getStatus())).count();

        s.setTotalEmployees((int) totalEmployees);
        s.setProcessedCount((int) processedCount);
        s.setPendingCount((int) pendingCount);
        s.setDraftCount((int) draftCount);

        // Calculate totals (only from PROCESSED records)
        List<PayrollRecord> processedRecords = records.stream()
                .filter(r -> "PROCESSED".equals(r.getStatus()))
                .collect(Collectors.toList());

        double totalNetPayroll = processedRecords.stream().mapToDouble(r -> r.getNetSalary() != null ? r.getNetSalary() : 0).sum();
        double totalGrossPayroll = processedRecords.stream().mapToDouble(r -> r.getGrossEarnings() != null ? r.getGrossEarnings() : 0).sum();
        double totalBasic = processedRecords.stream().mapToDouble(r -> r.getBasicSalary() != null ? r.getBasicSalary() : 0).sum();
        double totalHra = processedRecords.stream().mapToDouble(r -> r.getHra() != null ? r.getHra() : 0).sum();
        double totalDeductions = processedRecords.stream().mapToDouble(r -> r.getTotalDeductions() != null ? r.getTotalDeductions() : 0).sum();
        double totalPF = processedRecords.stream().mapToDouble(r -> r.getProvidentFund() != null ? r.getProvidentFund() : 0).sum();
        double totalTax = processedRecords.stream().mapToDouble(r ->
                (r.getProfessionalTax() != null ? r.getProfessionalTax() : 0) +
                        (r.getIncomeTax() != null ? r.getIncomeTax() : 0)).sum();

        s.setTotalNetPayroll(totalNetPayroll);
        s.setTotalGrossPayroll(totalGrossPayroll);
        s.setTotalBasic(totalBasic);
        s.setTotalHra(totalHra);
        s.setTotalDeductions(totalDeductions);
        s.setTotalPF(totalPF);
        s.setTotalTax(totalTax);
        s.setTotalAllowances(totalGrossPayroll - totalBasic);
        s.setAvgNetSalary(processedCount > 0 ? totalNetPayroll / processedCount : 0.0);

        log.info("Calculated aggregates - Total: {}, Processed: {}, Net: ₹{}, Basic: ₹{}, HRA: ₹{}",
                totalEmployees, processedCount, totalNetPayroll, totalBasic, totalHra);
    }

    private List<PayrollDashboardStats.MonthTrend> getMonthlyTrend(String yearMonth) {
        List<PayrollDashboardStats.MonthTrend> trendList = new ArrayList<>();

        try {
            String fromMonth = sixMonthsBack(yearMonth);

            // Get last 6 months of data
            for (int i = 5; i >= 0; i--) {
                LocalDate date = LocalDate.parse(yearMonth + "-01").minusMonths(i);
                String ym = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));

                List<PayrollRecord> monthRecords = repo.findByYearMonthAndIsDeletedFalse(ym);

                PayrollDashboardStats.MonthTrend trend = new PayrollDashboardStats.MonthTrend();
                trend.setYearMonth(ym);
                trend.setLabel(toLabel(ym));

                if (!monthRecords.isEmpty()) {
                    List<PayrollRecord> processed = monthRecords.stream()
                            .filter(r -> "PROCESSED".equals(r.getStatus()))
                            .collect(Collectors.toList());

                    double netPayroll = processed.stream().mapToDouble(r -> r.getNetSalary() != null ? r.getNetSalary() : 0).sum();
                    double grossPayroll = processed.stream().mapToDouble(r -> r.getGrossEarnings() != null ? r.getGrossEarnings() : 0).sum();

                    trend.setNetPayroll(netPayroll);
                    trend.setGrossPayroll(grossPayroll);
                    trend.setHeadCount(monthRecords.size());
                } else {
                    trend.setNetPayroll(0.0);
                    trend.setGrossPayroll(0.0);
                    trend.setHeadCount(0);
                }

                trendList.add(trend);
            }
        } catch (Exception e) {
            log.error("Error generating monthly trend", e);
        }

        return trendList;
    }

    private List<PayrollDashboardStats.DeptBreakdown> calculateDeptBreakdown(List<PayrollRecord> records) {
        List<PayrollDashboardStats.DeptBreakdown> deptList = new ArrayList<>();

        try {
            // Group by department (handle null departments)
            Map<String, List<PayrollRecord>> deptGroups = records.stream()
                    .filter(r -> "PROCESSED".equals(r.getStatus()))
                    .collect(Collectors.groupingBy(r -> {
                        if (r.getEmployee() != null && r.getEmployee().getDepartment() != null) {
                            return r.getEmployee().getDepartment().getName();
                        }
                        return "Unassigned";
                    }));

            for (Map.Entry<String, List<PayrollRecord>> entry : deptGroups.entrySet()) {
                PayrollDashboardStats.DeptBreakdown db = new PayrollDashboardStats.DeptBreakdown();
                db.setDepartment(entry.getKey());

                List<PayrollRecord> deptRecords = entry.getValue();
                double totalNet = deptRecords.stream().mapToDouble(r -> r.getNetSalary() != null ? r.getNetSalary() : 0).sum();
                int count = deptRecords.size();

                db.setTotalNet(totalNet);
                db.setCount(count);
                db.setAvgNet(count > 0 ? totalNet / count : 0.0);

                deptList.add(db);
            }

            // Sort by total net descending
            deptList.sort((a, b) -> Double.compare(b.getTotalNet(), a.getTotalNet()));

            log.info("Department breakdown calculated: {} departments", deptList.size());

        } catch (Exception e) {
            log.error("Error calculating department breakdown", e);
        }

        return deptList;
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
        int draft = s.getDraftCount() != null ? s.getDraftCount() : 0;

        if (emp == 0) {
            return "No payroll data found for " + toLabel(ym) + ". Generate records to get started.";
        }

        String label = toLabel(ym);

        if (pro == 0 && draft > 0) {
            return String.format(
                    "%s: %d employees in draft. Process payroll to calculate salaries.",
                    label, draft
            );
        }

        return String.format(
                "%s: ₹%.2fL net payroll across %d employees (%d processed, %d draft). Avg net salary ₹%.0f.",
                label, net / 100000, emp, pro, draft,
                s.getAvgNetSalary() != null ? s.getAvgNetSalary() : 0.0
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
}