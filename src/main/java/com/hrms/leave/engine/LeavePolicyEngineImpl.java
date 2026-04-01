package com.hrms.leave.engine;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.*;
import com.hrms.leave.dto.LeaveApplyRequest;
import com.hrms.leave.infrastructure.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeavePolicyEngineImpl implements LeavePolicyEngine {

    private final HolidayRepository holidayRepo;

    @Override
    public void validate(LeaveApplyRequest request, Employee emp, LeavePolicy policy) {

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Invalid date range");
        }

        if (Boolean.FALSE.equals(policy.getAllowBackdatedLeave())
                && request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Backdated leave not allowed");
        }

        if (Boolean.TRUE.equals(request.getIsHalfDay())
                && Boolean.FALSE.equals(policy.getAllowHalfDay())) {
            throw new RuntimeException("Half day not allowed");
        }
    }

    @Override
    public double calculateLeaveDays(LeaveApplyRequest request, LeavePolicy policy) {

        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();

        List<Holiday> holidays = holidayRepo.findByDateBetween(start, end);

        // 🔥 OPTIMIZATION
        Set<LocalDate> holidayDates = holidays.stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());

        double totalDays = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            boolean isWeekend = isWeekend(date);
            boolean isHoliday = holidayDates.contains(date);

            if (Boolean.FALSE.equals(policy.getHolidayIncludedInLeave()) && isHoliday) {
                continue;
            }

            if (Boolean.FALSE.equals(policy.getWeekendIncludedInLeave()) && isWeekend) {
                continue;
            }

            totalDays++;
        }

        // Half-day validation
        if (Boolean.TRUE.equals(request.getIsHalfDay())) {
            if (!start.equals(end)) {
                throw new RuntimeException("Half day only allowed for single day");
            }
            totalDays = 0.5;
        }

        // Sandwich logic
        if (Boolean.TRUE.equals(policy.getSandwichPolicyEnabled())) {
            totalDays = applySandwichPolicy(start, end, holidayDates, totalDays);
        }

        return totalDays;
    }
    // -----------------------------------------
    // Weekend Logic
    // -----------------------------------------
    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    // -----------------------------------------
    // Sandwich Policy Logic (🔥 IMPORTANT)
    // -----------------------------------------
    private double applySandwichPolicy(LocalDate start,
                                       LocalDate end,
                                       Set<LocalDate> holidayDates,
                                       double currentDays) {

        // No sandwich possible for single day
        if (start.equals(end)) {
            return currentDays;
        }

        boolean hasSandwichGap = false;

        for (LocalDate date = start.plusDays(1);
             date.isBefore(end);
             date = date.plusDays(1)) {

            boolean isWeekend = isWeekend(date);
            boolean isHoliday = holidayDates.contains(date);

            if (isWeekend || isHoliday) {
                hasSandwichGap = true;
            }
        }

        /*
         * Sandwich Rule:
         * If leave is applied across days AND
         * there are weekends/holidays in between,
         * then count full span
         *
         * Example:
         * Fri + Mon → Sat/Sun counted
         */
        if (hasSandwichGap) {
            return start.until(end).getDays() + 1;
        }

        return currentDays;
    }
}
