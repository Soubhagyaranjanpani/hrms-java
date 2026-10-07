package com.hrms.leave.engine;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.dto.LeaveApplyRequest;
import com.hrms.leave.infrastructure.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LeavePolicyEngineImpl implements LeavePolicyEngine {

    private final HolidayRepository holidayRepo;

    // 🔥 VALIDATION LOGIC
    @Override
    public void validate(LeaveApplyRequest request, Employee employee, LeavePolicy policy) {

        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();

        // ❌ Invalid date range
        if (start.isAfter(end)) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        // ❌ Backdated leave not allowed
        if (Boolean.FALSE.equals(policy.getAllowBackdatedLeave())
                && start.isBefore(LocalDate.now())) {
            throw new RuntimeException("Backdated leave not allowed");
        }

        // ❌ Max days validation
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;

        if (policy.getMaxLeaveDaysPerRequest() != null && days > policy.getMaxLeaveDaysPerRequest()) {
            throw new RuntimeException("Exceeds maximum allowed leave days");
        }

        // ❌ Half-day validation
        if (Boolean.TRUE.equals(request.getIsHalfDay()) && !start.equals(end)) {
            throw new RuntimeException("Half day allowed only for single day leave");
        }

        // ❌ Future extension: overlapping leave check (recommended)
    }

    // 🔥 CALCULATION LOGIC
    @Override
    public double calculateLeaveDays(LeaveApplyRequest request, LeavePolicy policy) {

        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();

        // Fetch holidays once
        Set<LocalDate> holidays = holidayRepo.findByDateBetween(start, end)
                .stream()
                .map(h -> h.getDate())
                .collect(Collectors.toSet());

        double totalDays = 0;

        // Base calculation
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            boolean isWeekend = isWeekend(date);
            boolean isHoliday = holidays.contains(date);

            if (Boolean.FALSE.equals(policy.getHolidayIncludedInLeave()) && isHoliday) {
                continue;
            }

            if (Boolean.FALSE.equals(policy.getWeekendIncludedInLeave()) && isWeekend) {
                continue;
            }

            totalDays++;
        }

        // Half-day override
        if (Boolean.TRUE.equals(request.getIsHalfDay())) {
            return 0.5;
        }

        // Sandwich logic
        if (Boolean.TRUE.equals(policy.getSandwichPolicyEnabled())) {
            totalDays = applySandwichPolicy(start, end, holidays, totalDays);
        }

        return totalDays;
    }

    // 🔥 SANDWICH LOGIC
    private double applySandwichPolicy(
            LocalDate start,
            LocalDate end,
            Set<LocalDate> holidays,
            double totalDays
    ) {

        Set<LocalDate> sandwichDays = new HashSet<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            if (isWeekend(date) || holidays.contains(date)) {

                LocalDate prev = date.minusDays(1);
                LocalDate next = date.plusDays(1);

                boolean hasLeaveBefore = !prev.isBefore(start);
                boolean hasLeaveAfter = !next.isAfter(end);

                if (hasLeaveBefore && hasLeaveAfter) {
                    sandwichDays.add(date);
                }
            }
        }

        return totalDays + sandwichDays.size();
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
