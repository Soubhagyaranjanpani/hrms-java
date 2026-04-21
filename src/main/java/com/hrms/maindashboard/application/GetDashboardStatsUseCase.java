package com.hrms.maindashboard.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.maindashboard.dto.DashboardStatsResponse;
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

    private final EmployeeRepository         empRepo;
    private final TaskRepository             taskRepo;
    private final PayrollRepository          payrollRepo;
    private final PerformanceReviewRepository perfRepo;

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

        // Recent 5 employees (by join date)
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

        // ── Task stats (with fallback) ─────────────────────────────────────
        List<Task> allTopLevelTasks = taskRepo.findByIsDeletedFalseAndParentTaskIsNull();

        int totalT = 0, pendingT = 0, doneT = 0, draftT = 0, progressT = 0, reviewT = 0, overdueT = 0;
        try {
            Object[] agg = taskRepo.aggregateStats();
            if (agg != null && agg.length >= 7) {
                totalT    = safe(agg[0]);
                pendingT  = safe(agg[1]);
                doneT     = safe(agg[2]);
                draftT    = safe(agg[3]);
                progressT = safe(agg[4]);
                reviewT   = safe(agg[5]);
                overdueT  = safe(agg[6]);
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
                    case "IN_PROGRESS"      -> progressT++;
                    case "COMPLETED"        -> doneT++;
                    case "DRAFT"            -> draftT++;
                    case "IN_REVIEW"        -> reviewT++;
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

        // Recent 5 tasks (by last updated or created)
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

        // ── AI summary ────────────────────────────────────────────────────
        r.setAiSummary(buildSummary(r));

        // ── REAL-TIME Activity feed from database ─────────────────────────
        r.setRecentActivity(buildRealTimeActivity());

        return r;
    }

    /**
     * Calculate payroll stats directly from PayrollRecord entities
     */
    private void calculatePayrollStats(DashboardStatsResponse r, String yearMonth) {
        try {
            List<PayrollRecord> records = payrollRepo.findByYearMonthAndIsDeletedFalse(yearMonth);

            if (records.isEmpty()) {
                r.setTotalPayrollThisMonth(0.0);
                r.setTotalBasicPayroll(0.0);
                r.setTotalDeductions(0.0);
                r.setAvgSalary(0.0);
                r.setPendingPayrollCount(0);
                r.setProcessedPayrollCount(0);
                r.setPayrollTrend(new ArrayList<>());
                r.setRecentPayslips(new ArrayList<>());
                return;
            }

            long processed = records.stream().filter(pr -> "PROCESSED".equals(pr.getStatus())).count();
            long pending = records.stream().filter(pr -> "PENDING".equals(pr.getStatus())).count();

            r.setProcessedPayrollCount((int) processed);
            r.setPendingPayrollCount((int) pending);

            List<PayrollRecord> processedRecords = records.stream()
                    .filter(pr -> "PROCESSED".equals(pr.getStatus()))
                    .toList();

            double totalNet = processedRecords.stream().mapToDouble(pr -> pr.getNetSalary() != null ? pr.getNetSalary() : 0).sum();
            double totalBasic = processedRecords.stream().mapToDouble(pr -> pr.getBasicSalary() != null ? pr.getBasicSalary() : 0).sum();
            double totalDeductions = processedRecords.stream().mapToDouble(pr -> pr.getTotalDeductions() != null ? pr.getTotalDeductions() : 0).sum();

            r.setTotalPayrollThisMonth(totalNet);
            r.setTotalBasicPayroll(totalBasic);
            r.setTotalDeductions(totalDeductions);
            r.setAvgSalary(processed > 0 ? totalNet / processed : 0.0);

            // Recent payslips - sorted by payment date or processed date
            List<DashboardStatsResponse.RecentPayslip> slips = processedRecords.stream()
                    .sorted((p1, p2) -> {
                        if (p1.getPaymentDate() != null && p2.getPaymentDate() != null) {
                            return p2.getPaymentDate().compareTo(p1.getPaymentDate());
                        }
                        return 0;
                    })
                    .limit(3)
                    .map(pr -> {
                        DashboardStatsResponse.RecentPayslip rp = new DashboardStatsResponse.RecentPayslip();
                        rp.setEmployeeName(clean(pr.getEmployee().getFirstName() + " " + pr.getEmployee().getLastName()));
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

                List<PayrollRecord> monthRecords = payrollRepo.findByYearMonthAndIsDeletedFalse(ym);

                DashboardStatsResponse.PayrollMonthTrend trend = new DashboardStatsResponse.PayrollMonthTrend();
                trend.setYearMonth(ym);
                trend.setLabel(date.format(DateTimeFormatter.ofPattern("MMM yy")));

                if (!monthRecords.isEmpty()) {
                    List<PayrollRecord> monthProcessed = monthRecords.stream()
                            .filter(pr -> "PROCESSED".equals(pr.getStatus()))
                            .toList();

                    double monthNet = monthProcessed.stream().mapToDouble(pr -> pr.getNetSalary() != null ? pr.getNetSalary() : 0).sum();
                    trend.setNetPayroll(monthNet);
                    trend.setHeadCount(monthRecords.size());
                } else {
                    trend.setNetPayroll(0.0);
                    trend.setHeadCount(0);
                }

                trendList.add(trend);
            }
            r.setPayrollTrend(trendList);

            log.info("Payroll calculated: Total=₹{}, Processed={}, Pending={}", totalNet, processed, pending);

        } catch (Exception ex) {
            log.error("Error calculating payroll stats: {}", ex.getMessage());
            setDefaultPayrollValues(r);
        }
    }

    /**
     * Calculate performance stats
     */
    private void calculatePerformanceStats(DashboardStatsResponse r) {
        try {
            Object[] pa = perfRepo.aggregateStats();
            if (pa != null && pa.length >= 4) {
                r.setAvgPerformanceRating(safeD(pa[0]));
                r.setPerformanceReviewsDone(safe(pa[1]));
                r.setTotalReviewsThisQuarter(safe(pa[2]));
                r.setOutstandingEmployees(safe(pa[3]));
            } else {
                r.setAvgPerformanceRating(0.0); r.setPerformanceReviewsDone(0);
                r.setTotalReviewsThisQuarter(0); r.setOutstandingEmployees(0);
            }
        } catch (Exception ex) {
            r.setAvgPerformanceRating(0.0); r.setPerformanceReviewsDone(0);
            r.setTotalReviewsThisQuarter(0); r.setOutstandingEmployees(0);
        }

        // Top performers - get most recent reviews
        try {
            List<PerformanceReview> recentReviews = perfRepo.findAll(
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "reviewedAt"))
            ).getContent();

            List<DashboardStatsResponse.TopPerformer> top = new ArrayList<>();
            Set<Long> seenEmployees = new HashSet<>();

            // Sort by rating and get top 3 unique employees
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

    /**
     * Build REAL-TIME activity feed from actual database records
     * No hardcoded data - everything comes from the database
     */
    private List<DashboardStatsResponse.ActivityItem> buildRealTimeActivity() {
        List<DashboardStatsResponse.ActivityItem> activities = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // 1. Recent Employee Joins (last 7 days)
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

        // 2. Recent Task Updates (last 7 days)
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

        // 3. Recent Payroll Processing (last 7 days)
        try {
            List<PayrollRecord> recentPayroll = payrollRepo.findAll().stream()
                    .filter(pr -> "PROCESSED".equals(pr.getStatus()))
                    .filter(pr -> pr.getUpdatedAt() != null && pr.getUpdatedAt().isAfter(sevenDaysAgo))
                    .sorted(Comparator.comparing(PayrollRecord::getUpdatedAt).reversed())
                    .limit(5)
                    .toList();

            for (PayrollRecord pr : recentPayroll) {
                DashboardStatsResponse.ActivityItem a = new DashboardStatsResponse.ActivityItem();
                a.setModule("PAYROLL");
                a.setType("process");
                String empName = pr.getEmployee() != null ?
                        clean(pr.getEmployee().getFirstName() + " " + pr.getEmployee().getLastName()) : "Employee";
                String salary = pr.getNetSalary() != null ?
                        "₹" + Math.round(pr.getNetSalary() / 1000) + "K" : "";
                a.setAction("Payroll processed for " + empName + " - " + salary);
                a.setActor(pr.getProcessedBy() != null ? pr.getProcessedBy() : "Payroll Admin");
                a.setTimestamp(formatTimeAgo(pr.getUpdatedAt()));
                activities.add(a);
            }
        } catch (Exception e) {
            log.error("Error fetching recent payroll: {}", e.getMessage());
        }

        // 4. Recent Performance Reviews (last 7 days)
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
                //                a.setActor(pr.getReviewedBy() != null ? pr.getReviewedBy() : "Manager");
                a.setActor("Manager");
                a.setTimestamp(formatTimeAgo(pr.getReviewedAt()));
                activities.add(a);
            }
        } catch (Exception e) {
            log.error("Error fetching recent performance reviews: {}", e.getMessage());
        }

        // Sort all activities by timestamp (most recent first) and limit to 10
        return activities.stream()
                .sorted((a1, a2) -> {
                    // Parse relative timestamps for sorting (simplified)
                    if (a1.getTimestamp().contains("Just now")) return -1;
                    if (a2.getTimestamp().contains("Just now")) return 1;
                    if (a1.getTimestamp().contains("minute")) return -1;
                    if (a2.getTimestamp().contains("minute")) return 1;
                    return a2.getTimestamp().compareTo(a1.getTimestamp());
                })
                .limit(10)
                .toList();
    }

    /**
     * Format timestamp as human-readable relative time
     */
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
        int    emp   = r.getTotalEmployees()        != null ? r.getTotalEmployees()        : 0;
        int    hire  = r.getNewHiresThisMonth()     != null ? r.getNewHiresThisMonth()     : 0;
        int    tasks = r.getTotalTasks()            != null ? r.getTotalTasks()            : 0;
        int    prog  = r.getInProgressTasks()       != null ? r.getInProgressTasks()       : 0;
        int    over  = r.getOverdueTasks()          != null ? r.getOverdueTasks()          : 0;
        double net   = r.getTotalPayrollThisMonth() != null ? r.getTotalPayrollThisMonth() : 0;
        double rat   = r.getAvgPerformanceRating()  != null ? r.getAvgPerformanceRating()  : 0;

        if (emp == 0) return "Welcome! Start by adding employees and configuring the system.";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Workforce at %d active employee%s", emp, emp > 1 ? "s" : ""));
        if (hire > 0) sb.append(String.format(", %d new hire%s this month", hire, hire > 1 ? "s" : ""));
        sb.append('.');
        if (tasks > 0) {
            sb.append(String.format(" %d task%s active", tasks, tasks > 1 ? "s" : ""));
            if (prog > 0) sb.append(String.format(", %d in progress", prog));
            if (over > 0) sb.append(String.format(", %d overdue", over));
            sb.append('.');
        }
        if (net > 0) sb.append(String.format(" Payroll: ₹%.2fL this month.", net / 100000));
        if (rat > 0) sb.append(String.format(" Avg performance: %.1f/5.", rat));
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
