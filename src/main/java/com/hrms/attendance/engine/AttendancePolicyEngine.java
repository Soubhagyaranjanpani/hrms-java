package com.hrms.attendance.engine;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.domain.AttendanceStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AttendancePolicyEngine {

    public void applyPolicy(Attendance att, AttendancePolicy policy) {

        if (att.getCheckIn() == null || att.getCheckOut() == null) {
            return;
        }

        double hours = Duration.between(
                att.getCheckIn(),
                att.getCheckOut()
        ).toMinutes() / 60.0;

        // 🔥 OVERNIGHT SHIFT HANDLING (crosses midnight)
        boolean isOvernight = hours < 0
                || Boolean.TRUE.equals(policy.getIsNightShift())
                || (policy.getShiftEnd() != null && policy.getShiftStart() != null && policy.getShiftEnd().isBefore(policy.getShiftStart()));

        if (hours < 0 && isOvernight) {
            hours += 24.0;
        }

        // Deduct break duration if applicable
        if (policy.getBreakMinutes() != null && policy.getBreakMinutes() > 0 && hours > (policy.getBreakMinutes() / 60.0)) {
            hours -= (policy.getBreakMinutes() / 60.0);
        }

        hours = Math.round(hours * 100.0) / 100.0;
        att.setWorkingHours(hours);

        // 🔥 STATUS CALCULATION (ORDER MATTERS)
        double halfDayThreshold = policy.getHalfDayThresholdHours() != null ? policy.getHalfDayThresholdHours() : 4.0;
        double fullDayThreshold = policy.getFullDayHours() != null ? policy.getFullDayHours() : 8.0;

        if (hours < halfDayThreshold) {
            att.setStatus(AttendanceStatus.ABSENT);
        } else if (hours < fullDayThreshold) {
            att.setStatus(AttendanceStatus.HALF_DAY);
        } else {
            att.setStatus(AttendanceStatus.PRESENT);
        }

        // 🔥 OVERTIME (only if overtime is allowed and worked hours exceed full day)
        if (Boolean.TRUE.equals(policy.getAllowOvertime()) && hours > fullDayThreshold) {
            att.setOvertimeHours(Math.round((hours - fullDayThreshold) * 100.0) / 100.0);
        } else {
            att.setOvertimeHours(0.0);
        }

        // 🔥 LATE LOGIN
        if (policy.getShiftStart() != null) {
            int grace = policy.getGraceMinutes() != null ? policy.getGraceMinutes() : 0;
            if (att.getCheckIn().isAfter(policy.getShiftStart().plusMinutes(grace))) {
                att.setIsLate(true);
            } else {
                att.setIsLate(false);
            }
        }

        // 🔥 EARLY EXIT
        if (policy.getShiftEnd() != null) {
            if (isOvernight) {
                // For overnight shifts (e.g. 12:00 PM to 09:00 AM next morning)
                // If checkout is after shiftStart on start day, or before shiftEnd on next morning, it is early exit
                if (att.getCheckOut().isAfter(policy.getShiftStart()) || att.getCheckOut().isBefore(policy.getShiftEnd())) {
                    att.setIsEarlyExit(true);
                } else {
                    att.setIsEarlyExit(false);
                }
            } else {
                if (att.getCheckOut().isBefore(policy.getShiftEnd())) {
                    att.setIsEarlyExit(true);
                } else {
                    att.setIsEarlyExit(false);
                }
            }
        }
    }
}
