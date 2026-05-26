// File: com/hrms/maindashboard/application/GetDashboardStatsUseCase.java
package com.hrms.maindashboard.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.maindashboard.dto.DashboardData;
import com.hrms.maindashboard.dto.DashboardStatsResponse;
import com.hrms.maindashboard.dto.DeptData;
import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.infrastructure.PayrollRepository;
import com.hrms.task.domain.PerformanceReview;
import com.hrms.task.domain.Task;
import com.hrms.task.infrastructure.PerformanceReviewRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetDashboardStatsUseCase {

    private final EmployeeRepository empRepo;
    private final TaskRepository taskRepo;
    private final PayrollRepository payrollRepo;
    private final PerformanceReviewRepository perfRepo;
    private final AIService aiService;

    public DashboardStatsResponse execute(String principalEmail) {

        DashboardStatsResponse r = new DashboardStatsResponse();

        // ── Greeting ──────────────────────────────────────────────────────
        Employee me = empRepo.findByEmailAndIsDeletedFalse(principalEmail).orElse(null);
        r.setLoggedInUserName(me != null ? clean(me.getFirstName() + " " + me.getLastName()) : "User");
        r.setLoggedInUserRole(me != null && me.getRole() != null ? me.getRole().getName() : "");
        r.setCurrentMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        r.setCurrentDate(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));

        // ── Employee stats ────────────────────────────────────────────────
        List<Employee> allActive = empRepo.findByIsActiveTrueAndIsDeletedFalse();
        int totalEmp = allActive.size();
        r.setTotalEmployees(totalEmp);
        r.setActiveEmployees(totalEmp);

        String thisYM = yearMonth();
        long newHires = allActive.stream()
                .filter(e -> e.getJoiningDate() != null && yearMonth(e.getJoiningDate()).equals(thisYM))
                .count();
        r.setNewHiresThisMonth((int) newHires);
        r.setEmployeeGrowthPct(totalEmp > 0 ? Math.round((newHires * 100.0 / totalEmp) * 10) / 10.0 : 0.0);

        // Dept headcounts
        Map<String, Long> deptMap = allActive.stream()
                .filter(e -> e.getDepartment() != null)
                .collect(Collectors.groupingBy(e -> e.getDepartment().getName(), Collectors.counting()));
        List<DashboardStatsResponse.DeptHeadcount> deptList = deptMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(en -> {
                    DashboardStatsResponse.DeptHeadcount d = new DashboardStatsResponse.DeptHeadcount();
                    d.setDepartment(en.getKey());
                    d.setCount(en.getValue().intValue());
                    d.setPct(totalEmp > 0 ? Math.round(en.getValue() * 1000.0 / totalEmp) / 10.0 : 0);
                    return d;
                }).toList();
        r.setDeptHeadcounts(deptList);

        // Recent 5 employees
        List<DashboardStatsResponse.RecentEmployee> recentEmp = allActive.stream()
                .filter(e -> e.getJoiningDate() != null)
                .sorted(Comparator.comparing(Employee::getJoiningDate).reversed())
                .limit(5)
                .map(e -> {
                    DashboardStatsResponse.RecentEmployee re = new DashboardStatsResponse.RecentEmployee();
                    re.setId(e.getId());
                    re.setName(clean(e.getFirstName() + " " + e.getLastName()));
                    re.setRole(e.getRole() != null ? e.getRole().getName() : "");
                    re.setDepartment(e.getDepartment() != null ? e.getDepartment().getName() : "");
                    re.setJoinedDate(e.getJoiningDate().format(DateTimeFormatter.ofPattern("d MMM yyyy")));
                    re.setEmployeeCode(e.getEmployeeCode());
                    return re;
                }).toList();
        r.setRecentEmployees(recentEmp);

        // ── Task stats ─────────────────────────────────────────────────────
        List<Task> allTopLevelTasks = taskRepo.findByIsDeletedFalseAndParentTaskIsNull();

        int totalT = 0, pendingT = 0, doneT = 0, draftT = 0, progressT = 0, reviewT = 0, overdueT = 0;
        try {
            Object[] agg = taskRepo.aggregateStats();
            if (agg != null && agg.length >= 7) {
                totalT = safe(agg[0]);
                pendingT = safe(agg[1]);
                doneT = safe(agg[2]);
                draftT = safe(agg[3]);
                progressT = safe(agg[4]);
                reviewT = safe(agg[5]);
                overdueT = safe(agg[6]);
            }
        } catch (Exception ex) {
            log.error("Dashboard aggregateStats() failed: {}", ex.getMessage());
        }

        boolean allZero = pendingT == 0 && progressT == 0 && doneT == 0 && reviewT == 0 && draftT == 0;
        if (allZero && !allTopLevelTasks.isEmpty()) {
            totalT = allTopLevelTasks.size();
            LocalDateTime now = LocalDateTime.now();
            for (Task t : allTopLevelTasks) {
                String st = t.getStatus() != null ? t.getStatus().name() : "";
                switch (st) {
                    case "PENDING_APPROVAL" -> pendingT++;
                    case "IN_PROGRESS" -> progressT++;
                    case "COMPLETED" -> doneT++;
                    case "DRAFT" -> draftT++;
                    case "IN_REVIEW" -> reviewT++;
                }
                if (t.getDueDate() != null && t.getDueDate().isBefore(now)
                        && !"COMPLETED".equals(st) && !"REJECTED".equals(st)) {
                    overdueT++;
                }
            }
        }

        r.setTotalTasks(totalT);
        r.setPendingTasks(pendingT);
        r.setCompletedTasksThisMonth(doneT);
        r.setDraftTasks(draftT);
        r.setInProgressTasks(progressT);
        r.setInReviewTasks(reviewT);
        r.setOverdueTasks(overdueT);
        r.setTaskCompletionRate(totalT > 0 ? Math.round(doneT * 1000.0 / totalT) / 10.0 : 0.0);

        // Recent 5 tasks
        List<DashboardStatsResponse.RecentTask> recentTasks = allTopLevelTasks.stream()
                .sorted((t1, t2) -> {
                    LocalDateTime d1 = t1.getUpdatedAt() != null ? t1.getUpdatedAt() : t1.getCreatedAt();
                    LocalDateTime d2 = t2.getUpdatedAt() != null ? t2.getUpdatedAt() : t2.getCreatedAt();
                    return d2.compareTo(d1);
                })
                .limit(5)
                .map(t -> {
                    DashboardStatsResponse.RecentTask rt = new DashboardStatsResponse.RecentTask();
                    rt.setId(t.getId());
                    rt.setTitle(t.getTitle());
                    rt.setStatus(t.getStatus() != null ? t.getStatus().name() : "");
                    rt.setPriority(t.getPriority() != null ? t.getPriority().name() : "");
                    rt.setAssignedTo(t.getAssignedTo() != null ? clean(t.getAssignedTo().getFirstName()) : "");
                    rt.setDueDate(t.getDueDate() != null
                            ? t.getDueDate().format(DateTimeFormatter.ofPattern("d MMM")) : "");
                    rt.setProgress(t.getProgress() != null ? t.getProgress() : 0);
                    rt.setUpdatedAt(t.getUpdatedAt() != null ? t.getUpdatedAt() : t.getCreatedAt());
                    return rt;
                }).toList();
        r.setRecentTasks(recentTasks);

        // ── Payroll stats ─────────────────────────────────────────────────
        calculatePayrollStats(r, thisYM);

        // ── Performance stats ─────────────────────────────────────────────
        calculatePerformanceStats(r);

        // ── REAL-TIME Activity feed ───────────────────────────────────────
        r.setRecentActivity(buildRealTimeActivity());

        // ── AI-POWERED Insight ────────────────────────────────────────────
        try {
            DashboardData aiData = new DashboardData();
            aiData.setTotalEmployees(r.getTotalEmployees() != null ? r.getTotalEmployees() : 0);
            aiData.setNewHiresThisMonth(r.getNewHiresThisMonth() != null ? r.getNewHiresThisMonth() : 0);
            aiData.setEmployeeGrowthPct(r.getEmployeeGrowthPct() != null ? r.getEmployeeGrowthPct() : 0.0);
            aiData.setTotalTasks(r.getTotalTasks() != null ? r.getTotalTasks() : 0);
            aiData.setInProgressTasks(r.getInProgressTasks() != null ? r.getInProgressTasks() : 0);
            aiData.setInReviewTasks(r.getInReviewTasks() != null ? r.getInReviewTasks() : 0);
            aiData.setOverdueTasks(r.getOverdueTasks() != null ? r.getOverdueTasks() : 0);
            aiData.setCompletedTasks(r.getCompletedTasksThisMonth() != null ? r.getCompletedTasksThisMonth() : 0);
            aiData.setTaskCompletionRate(r.getTaskCompletionRate() != null ? r.getTaskCompletionRate() : 0.0);
            aiData.setTotalPayrollThisMonth(r.getTotalPayrollThisMonth() != null ? r.getTotalPayrollThisMonth() : 0.0);
            aiData.setPendingPayrollCount(r.getPendingPayrollCount() != null ? r.getPendingPayrollCount() : 0);
            aiData.setProcessedPayrollCount(r.getProcessedPayrollCount() != null ? r.getProcessedPayrollCount() : 0);
            aiData.setAvgSalary(r.getAvgSalary() != null ? r.getAvgSalary() : 0.0);
            aiData.setAvgPerformanceRating(r.getAvgPerformanceRating() != null ? r.getAvgPerformanceRating() : 0.0);
            aiData.setOutstandingEmployees(r.getOutstandingEmployees() != null ? r.getOutstandingEmployees() : 0);
            aiData.setPerformanceReviewsDone(r.getPerformanceReviewsDone() != null ? r.getPerformanceReviewsDone() : 0);
            aiData.setDepts(convertDepts(r.getDeptHeadcounts()));

            String aiInsight = aiService.generateDashboardInsights(aiData);
            r.setAiSummary(aiInsight);
            log.info("AI Insight generated: {}", aiInsight);
        } catch (Exception e) {
            log.error("AI Service failed: {}", e.getMessage());
            r.setAiSummary(buildSummary(r));
        }

        return r;
    }

    private List<DeptData> convertDepts(List<DashboardStatsResponse.DeptHeadcount> depts) {
        if (depts == null || depts.isEmpty()) return new ArrayList<>();
        return depts.stream()
                .map(d -> DeptData.builder()
                        .department(d.getDepartment() != null ? d.getDepartment() : "Unknown")
                        .count(d.getCount() != null ? d.getCount() : 0)
                        .pct(d.getPct() != null ? d.getPct() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }

    // ========== PAYROLL STATS - FIXED (Shows ALL months) ==========
    private void calculatePayrollStats(DashboardStatsResponse r, String yearMonth) {
        try {
            String sixMonthsAgo = LocalDate.now().minusMonths(5)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM"));

            List<PayrollRecord> allRecords = payrollRepo.findAll().stream()
                    .filter(pr -> pr.getIsDeleted() == null || !pr.getIsDeleted())
                    .filter(pr -> pr.getYearMonth() != null)
                    .filter(pr -> pr.getYearMonth().compareTo(sixMonthsAgo) >= 0)
                    .toList();

            if (allRecords.isEmpty()) {
                setDefaultPayrollValues(r);
                return;
            }

            // 🔥 FIX: Find the latest month that actually has records
            String latestMonthWithData = allRecords.stream()
                    .map(PayrollRecord::getYearMonth)
                    .max(String::compareTo)
                    .orElse(yearMonth);

            // Use records from the latest month with data for summary cards
            List<PayrollRecord> displayMonthRecords = allRecords.stream()
                    .filter(pr -> latestMonthWithData.equals(pr.getYearMonth()))
                    .toList();

            // Count ALL statuses
            long processed = displayMonthRecords.stream().filter(pr -> "PROCESSED".equals(pr.getStatus())).count();
            long approved = displayMonthRecords.stream().filter(pr -> "APPROVED".equals(pr.getStatus())).count();
            long pending = displayMonthRecords.stream().filter(pr -> "PENDING".equals(pr.getStatus())).count();
            long draft = displayMonthRecords.stream().filter(pr -> "DRAFT".equals(pr.getStatus())).count();

            r.setProcessedPayrollCount((int) processed);
            r.setPendingPayrollCount((int) (draft + pending + approved));

            // Calculate from the display month
            double totalNet = displayMonthRecords.stream()
                    .mapToDouble(pr -> pr.getNetSalary() != null ? pr.getNetSalary() : 0).sum();
            double totalBasic = displayMonthRecords.stream()
                    .mapToDouble(pr -> pr.getBasicSalary() != null ? pr.getBasicSalary() : 0).sum();
            double totalDeductions = displayMonthRecords.stream()
                    .mapToDouble(pr -> pr.getTotalDeductions() != null ? pr.getTotalDeductions() : 0).sum();

            r.setTotalPayrollThisMonth(totalNet);
            r.setTotalBasicPayroll(totalBasic);
            r.setTotalDeductions(totalDeductions);
            r.setAvgSalary(displayMonthRecords.size() > 0 ? totalNet / displayMonthRecords.size() : 0.0);

            // Recent payslips - from ALL months, sorted by most recent
            List<DashboardStatsResponse.RecentPayslip> slips = allRecords.stream()
                    .sorted((p1, p2) -> {
                        LocalDateTime d1 = p1.getCreatedAt() != null ? p1.getCreatedAt() : LocalDateTime.MIN;
                        LocalDateTime d2 = p2.getCreatedAt() != null ? p2.getCreatedAt() : LocalDateTime.MIN;
                        return d2.compareTo(d1);
                    })
                    .limit(3)
                    .map(pr -> {
                        DashboardStatsResponse.RecentPayslip rp = new DashboardStatsResponse.RecentPayslip();
                        if (pr.getEmployee() != null) {
                            rp.setEmployeeName(clean(pr.getEmployee().getFirstName() + " " +
                                    (pr.getEmployee().getLastName() != null ? pr.getEmployee().getLastName() : "")));
                        } else {
                            rp.setEmployeeName("Employee #" + pr.getId());
                        }
                        rp.setPayrollMonth(pr.getPayrollMonth());
                        rp.setNetSalary(pr.getNetSalary());
                        rp.setStatus(pr.getStatus());
                        rp.setPaymentDate(pr.getPaymentDate());
                        return rp;
                    }).toList();
            r.setRecentPayslips(slips);

            // 6-month trend
            List<DashboardStatsResponse.PayrollMonthTrend> trendList = new ArrayList<>();
            for (int i = 5; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusMonths(i);
                String ym = String.format("%04d-%02d", date.getYear(), date.getMonthValue());

                List<PayrollRecord> monthRecords = allRecords.stream()
                        .filter(pr -> ym.equals(pr.getYearMonth()))
                        .toList();

                DashboardStatsResponse.PayrollMonthTrend trend = new DashboardStatsResponse.PayrollMonthTrend();
                trend.setYearMonth(ym);
                trend.setLabel(date.format(DateTimeFormatter.ofPattern("MMM yy")));

                if (!monthRecords.isEmpty()) {
                    double monthNet = monthRecords.stream()
                            .mapToDouble(pr -> pr.getNetSalary() != null ? pr.getNetSalary() : 0).sum();
                    trend.setNetPayroll(monthNet);
                    trend.setHeadCount(monthRecords.size());
                } else {
                    trend.setNetPayroll(0.0);
                    trend.setHeadCount(0);
                }
                trendList.add(trend);
            }
            r.setPayrollTrend(trendList);

            log.info("Payroll: DisplayMonth={}, Total=₹{}, Processed={}, Pending={}",
                    latestMonthWithData, totalNet, processed, draft + pending + approved);

        } catch (Exception ex) {
            log.error("Error calculating payroll stats: {}", ex.getMessage(), ex);
            setDefaultPayrollValues(r);
        }
    }

    // ========== PERFORMANCE STATS ==========
    private void calculatePerformanceStats(DashboardStatsResponse r) {
        try {
            Object[] pa = perfRepo.aggregateStats();
            if (pa != null && pa.length >= 4) {
                r.setAvgPerformanceRating(safeD(pa[0]));
                r.setPerformanceReviewsDone(safe(pa[1]));
                r.setTotalReviewsThisQuarter(safe(pa[2]));
                r.setOutstandingEmployees(safe(pa[3]));
            } else {
                r.setAvgPerformanceRating(0.0);
                r.setPerformanceReviewsDone(0);
                r.setTotalReviewsThisQuarter(0);
                r.setOutstandingEmployees(0);
            }
        } catch (Exception ex) {
            r.setAvgPerformanceRating(0.0);
            r.setPerformanceReviewsDone(0);
            r.setTotalReviewsThisQuarter(0);
            r.setOutstandingEmployees(0);
        }

        // Top performers
        try {
            List<PerformanceReview> recentReviews = perfRepo.findAll(
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "reviewedAt"))
            ).getContent();

            List<DashboardStatsResponse.TopPerformer> top = new ArrayList<>();
            Set<Long> seenEmployees = new HashSet<>();

            recentReviews.stream()
                    .filter(pr -> pr != null && pr.getEmployee() != null)
                    .sorted((r1, r2) -> Double.compare(r2.getRating(), r1.getRating()))
                    .forEach(perf -> {
                        Long empId = perf.getEmployee().getId();
                        if (!seenEmployees.contains(empId) && top.size() < 3) {
                            seenEmployees.add(empId);
                            DashboardStatsResponse.TopPerformer tp = new DashboardStatsResponse.TopPerformer();
                            tp.setRank(top.size() + 1);
                            tp.setEmployeeName(clean(perf.getEmployee().getFirstName() + " " + perf.getEmployee().getLastName()));
                            tp.setDepartment(perf.getEmployee().getDepartment() != null ? perf.getEmployee().getDepartment().getName() : "");
                            tp.setRating(perf.getRating());
                            tp.setBadge(ratingBadge(perf.getRating()));
                            tp.setReviewDate(perf.getReviewedAt() != null ? perf.getReviewedAt().toLocalDate() : null);
                            top.add(tp);
                        }
                    });

            r.setTopPerformers(top);
        } catch (Exception ex) {
            r.setTopPerformers(new ArrayList<>());
        }

        // Rating distribution
        try {
            List<Object[]> distData = perfRepo.ratingDistribution();
            List<DashboardStatsResponse.RatingDistribution> dist = (distData != null ? distData : List.<Object[]>of())
                    .stream()
                    .filter(row -> row != null && row.length >= 2 && row[0] != null)
                    .map(row -> {
                        DashboardStatsResponse.RatingDistribution rd = new DashboardStatsResponse.RatingDistribution();
                        rd.setLabel(row[0].toString());
                        rd.setCount(safe(row[1]));
                        return rd;
                    }).toList();
            int totalP = dist.stream().mapToInt(DashboardStatsResponse.RatingDistribution::getCount).sum();
            dist.forEach(d -> d.setPct(totalP > 0 ? Math.round(d.getCount() * 1000.0 / totalP) / 10.0 : 0.0));
            r.setRatingDistribution(dist);
        } catch (Exception ex) {
            r.setRatingDistribution(new ArrayList<>());
        }
    }

    // ========== REAL-TIME ACTIVITY - FIXED SORTING ==========
    private List<DashboardStatsResponse.ActivityItem> buildRealTimeActivity() {
        List<DashboardStatsResponse.ActivityItem> activities = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // 1. Recent Employee Joins
        try {
            List<Employee> recentJoins = empRepo.findByIsActiveTrueAndIsDeletedFalse().stream()
                    .filter(e -> e.getJoiningDate() != null)
                    .filter(e -> e.getJoiningDate().isAfter(sevenDaysAgo.toLocalDate()))
                    .sorted(Comparator.comparing(Employee::getJoiningDate).reversed())
                    .limit(5)
                    .toList();

            for (Employee e : recentJoins) {
                DashboardStatsResponse.ActivityItem a = new DashboardStatsResponse.ActivityItem();
                a.setModule("EMPLOYEE");
                a.setType("create");
                a.setAction(clean(e.getFirstName() + " " + e.getLastName()) + " joined as " +
                        (e.getRole() != null ? e.getRole().getName() : "Employee"));
                a.setActor("HR System");
                a.setTimestamp(formatTimeAgo(e.getJoiningDate().atStartOfDay()));
                activities.add(a);
            }
        } catch (Exception e) {
            log.error("Error fetching recent employee joins: {}", e.getMessage());
        }

        // 2. Recent Task Updates
        try {
            List<Task> recentTaskUpdates = taskRepo.findByIsDeletedFalse().stream()
                    .filter(t -> t.getUpdatedAt() != null && t.getUpdatedAt().isAfter(sevenDaysAgo))
                    .sorted(Comparator.comparing(Task::getUpdatedAt).reversed())
                    .limit(5)
                    .toList();

            for (Task t : recentTaskUpdates) {
                DashboardStatsResponse.ActivityItem a = new DashboardStatsResponse.ActivityItem();
                a.setModule("TASK");
                a.setType("update");
                String statusText = t.getStatus() != null ? t.getStatus().name().toLowerCase().replace("_", " ") : "updated";
                a.setAction("Task \"" + truncate(t.getTitle()) + "\" marked as " + statusText);
                a.setActor(t.getAssignedTo() != null ? clean(t.getAssignedTo().getFirstName()) : "System");
                a.setTimestamp(formatTimeAgo(t.getUpdatedAt()));
                activities.add(a);
            }
        } catch (Exception e) {
            log.error("Error fetching recent task updates: {}", e.getMessage());
        }

        // 3. Recent Payroll - FIXED: null check on getCreatedAt()
        try {
            List<PayrollRecord> allPayroll = payrollRepo.findAll();
            List<PayrollRecord> recentPayroll = allPayroll.stream()
                    .filter(pr -> pr.getCreatedAt() != null)
                    .filter(pr -> pr.getCreatedAt().isAfter(sevenDaysAgo))
                    .sorted(Comparator.comparing(PayrollRecord::getCreatedAt).reversed())
                    .limit(5)
                    .toList();

            for (PayrollRecord pr : recentPayroll) {
                DashboardStatsResponse.ActivityItem a = new DashboardStatsResponse.ActivityItem();
                a.setModule("PAYROLL");
                a.setType(pr.getStatus() != null ? pr.getStatus().toLowerCase() : "generated");

                String empName = "Employee";
                if (pr.getEmployee() != null) {
                    empName = clean(pr.getEmployee().getFirstName());
                }

                String salary = pr.getNetSalary() != null ?
                        "₹" + Math.round(pr.getNetSalary() / 1000) + "K" : "";

                String action;
                String st = pr.getStatus();
                if ("PROCESSED".equals(st)) {
                    action = "Payroll processed for " + empName + " - " + salary;
                } else if ("APPROVED".equals(st)) {
                    action = "Payroll approved for " + empName + " - " + salary;
                } else if ("PENDING".equals(st)) {
                    action = "Payroll submitted for " + empName + " - " + salary;
                } else {
                    action = "Payroll generated for " + empName + " - " + salary;
                }

                a.setAction(action);
                a.setActor(pr.getProcessedBy() != null ? pr.getProcessedBy() : "Payroll Admin");
                a.setTimestamp(formatTimeAgo(pr.getCreatedAt()));
                activities.add(a);
            }
        } catch (Exception e) {
            log.error("Error fetching recent payroll: {}", e.getMessage());
        }

        // 4. Recent Performance Reviews
        try {
            List<PerformanceReview> recentReviews = perfRepo.findAll(
                            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "reviewedAt"))
                    ).getContent().stream()
                    .filter(pr -> pr.getReviewedAt() != null && pr.getReviewedAt().isAfter(sevenDaysAgo))
                    .limit(5)
                    .toList();

            for (PerformanceReview pr : recentReviews) {
                DashboardStatsResponse.ActivityItem a = new DashboardStatsResponse.ActivityItem();
                a.setModule("PERFORMANCE");
                a.setType("rating");
                String empName = pr.getEmployee() != null ?
                        clean(pr.getEmployee().getFirstName() + " " + pr.getEmployee().getLastName()) : "Employee";
                String badge = ratingBadge(pr.getRating());
                a.setAction(empName + " received " + badge + " rating (" + pr.getRating() + "/5)");
                a.setActor("Manager");
                a.setTimestamp(formatTimeAgo(pr.getReviewedAt()));
                activities.add(a);
            }
        } catch (Exception e) {
            log.error("Error fetching recent performance reviews: {}", e.getMessage());
        }

        // FIXED: Sort by actual time - most recent first
        return activities.stream()
                .sorted((a1, a2) -> {
                    int mins1 = extractMinutes(a1.getTimestamp());
                    int mins2 = extractMinutes(a2.getTimestamp());
                    return Integer.compare(mins1, mins2); // Lower = more recent
                })
                .limit(10)
                .toList();
    }

    /**
     * Extract approximate minutes from timestamp string for sorting
     * "Just now" = 0, "5 min ago" = 5, "2 hour ago" = 120
     */
    private int extractMinutes(String timestamp) {
        if (timestamp == null) return 9999;
        if (timestamp.contains("Just now")) return 0;
        if (timestamp.contains("min")) {
            try {
                return Integer.parseInt(timestamp.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                return 60;
            }
        }
        if (timestamp.contains("hour")) {
            try {
                return Integer.parseInt(timestamp.replaceAll("[^0-9]", "")) * 60;
            } catch (Exception e) {
                return 120;
            }
        }
        if (timestamp.contains("day")) {
            try {
                return Integer.parseInt(timestamp.replaceAll("[^0-9]", "")) * 1440;
            } catch (Exception e) {
                return 2880;
            }
        }
        return 9999; // Old items at bottom
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "Recently";
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        if (hours < 24) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        if (days < 7) return days + " day" + (days > 1 ? "s" : "") + " ago";
        return dateTime.format(DateTimeFormatter.ofPattern("d MMM"));
    }

    private void setDefaultPayrollValues(DashboardStatsResponse r) {
        r.setTotalPayrollThisMonth(0.0);
        r.setTotalBasicPayroll(0.0);
        r.setTotalDeductions(0.0);
        r.setAvgSalary(0.0);
        r.setPendingPayrollCount(0);
        r.setProcessedPayrollCount(0);
        r.setPayrollTrend(new ArrayList<>());
        r.setRecentPayslips(new ArrayList<>());
    }

    private String buildSummary(DashboardStatsResponse r) {
        int emp = r.getTotalEmployees() != null ? r.getTotalEmployees() : 0;
        int tasks = r.getTotalTasks() != null ? r.getTotalTasks() : 0;
        double net = r.getTotalPayrollThisMonth() != null ? r.getTotalPayrollThisMonth() : 0;
        if (emp == 0) return "Welcome! Start by adding employees.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d employees", emp));
        if (tasks > 0) sb.append(String.format(", %d tasks", tasks));
        if (net > 0) sb.append(String.format(", ₹%.2fL payroll", net / 100000));
        sb.append('.');
        return sb.toString();
    }

    private String truncate(String s) {
        final int maxLen = 30;
        if (s == null) return "";
        return s.length() > maxLen ? s.substring(0, maxLen - 3) + "..." : s;
    }

    private String ratingBadge(Double v) {
        if (v == null) return "";
        if (v >= 4.8) return "Outstanding";
        if (v >= 4.4) return "Excellent";
        if (v >= 4.0) return "Great";
        if (v >= 3.5) return "Good";
        return "Satisfactory";
    }

    private String yearMonth() {
        LocalDate n = LocalDate.now();
        return String.format("%04d-%02d", n.getYear(), n.getMonthValue());
    }

    private String yearMonth(LocalDate d) {
        return d == null ? "" : String.format("%04d-%02d", d.getYear(), d.getMonthValue());
    }

    private int safe(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return 0; }
    }

    private double safeD(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }

    private String clean(String s) {
        return s == null ? "" : s.replace("null", "").trim();
    }
}