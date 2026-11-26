package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.task.domain.SubmissionStatus;
import com.hrms.task.domain.TaskSubmission;
import com.hrms.task.dto.DailyProgressPoint;
import com.hrms.task.dto.EmployeeProgressResponse;
import com.hrms.task.infrastructure.TaskSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetEmployeeProgressUseCase {

    private final AccessScopeService scope;
    private final EmployeeRepository employeeRepo;
    private final TaskSubmissionRepository submissionRepo;
    private final GetEmployeeTaskStatsUseCase taskStatsUseCase;

    /** employeeId == null -> the logged-in user's own progress. */
    public EmployeeProgressResponse execute(String viewerEmail, Long employeeId, LocalDate from, LocalDate to) {
        Employee viewer = scope.currentUser(viewerEmail);
        Employee target = employeeId == null ? viewer
                : employeeRepo.findById(employeeId)
                .orElseThrow(() -> scope.badRequest("Employee not found: " + employeeId));

        scope.assertCanViewEmployee(viewer, target);

        LocalDate[] range = normalizeRange(from, to);
        List<TaskSubmission> subs = submissionRepo
                .findBySubmittedByIdAndSubmissionDateBetween(target.getId(), range[0], range[1]);

        return build(target, subs, range[0], range[1]);
    }

    static LocalDate[] normalizeRange(LocalDate from, LocalDate to) {
        LocalDate end = to != null ? to : LocalDate.now();
        LocalDate start = from != null ? from : end.minusDays(29);
        if (start.isAfter(end)) throw new IllegalArgumentException("'from' must be before 'to'");
        if (ChronoUnit.DAYS.between(start, end) > 366) throw new IllegalArgumentException("Range too large (max 1 year)");
        return new LocalDate[]{start, end};
    }

    EmployeeProgressResponse build(Employee emp, List<TaskSubmission> subs, LocalDate from, LocalDate to) {

        Map<LocalDate, List<TaskSubmission>> byDay =
                subs.stream().collect(Collectors.groupingBy(TaskSubmission::getSubmissionDate));

        List<DailyProgressPoint> daily = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            List<TaskSubmission> day = byDay.getOrDefault(d, List.of());
            DailyProgressPoint p = new DailyProgressPoint();
            p.setDate(d);
            p.setSubmissions(day.size());
            p.setApproved((int) day.stream().filter(s -> s.getStatus() == SubmissionStatus.APPROVED).count());
            p.setHoursSpent(round1(day.stream().mapToDouble(s -> s.getHoursSpent() != null ? s.getHoursSpent() : 0).sum()));
            p.setAvgProgress(round1(day.stream().mapToInt(s -> s.getProgressPercent() != null ? s.getProgressPercent() : 0)
                    .average().orElse(0)));
            daily.add(p);
        }

        long days = ChronoUnit.DAYS.between(from, to) + 1;
        double totalHours = subs.stream().mapToDouble(s -> s.getHoursSpent() != null ? s.getHoursSpent() : 0).sum();

        EmployeeProgressResponse r = new EmployeeProgressResponse();
        r.setEmployeeId(emp.getId());
        r.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());
        r.setRole(scope.roleOf(emp).name());
        if (emp.getDepartment() != null) {
            r.setDepartmentId(emp.getDepartment().getId());
            r.setDepartment(emp.getDepartment().getName());
        }
        r.setFrom(from);
        r.setTo(to);
        r.setTotalSubmissions((long) subs.size());
        r.setApprovedSubmissions(subs.stream().filter(s -> s.getStatus() == SubmissionStatus.APPROVED).count());
        r.setRejectedSubmissions(subs.stream().filter(s -> s.getStatus() == SubmissionStatus.REJECTED).count());
        r.setLateSubmissions(subs.stream().filter(s -> Boolean.TRUE.equals(s.getLate())).count());
        r.setActiveDays(byDay.size());
        r.setAvgDailySubmissions(round2((double) subs.size() / days));
        r.setAvgDailyHours(round2(totalHours / days));
        r.setTaskStats(taskStatsUseCase.execute(emp.getId()));
        r.setDaily(daily);
        return r;
    }

    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }
    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}
