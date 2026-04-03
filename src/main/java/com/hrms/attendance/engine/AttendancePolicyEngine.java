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

        att.setWorkingHours(hours);

        // 🔥 STATUS CALCULATION (ORDER MATTERS)

        if (hours < policy.getHalfDayThresholdHours()) {
            att.setStatus(AttendanceStatus.ABSENT);

        } else if (hours < policy.getFullDayHours()) {
            att.setStatus(AttendanceStatus.HALF_DAY);

        } else {
            att.setStatus(AttendanceStatus.PRESENT);
        }

        // 🔥 OVERTIME (only for full day)
        if (hours > policy.getFullDayHours()) {
            att.setOvertimeHours(hours - policy.getFullDayHours());
        } else {
            att.setOvertimeHours(0.0);
        }

        // 🔥 LATE LOGIN
        if (policy.getShiftStart() != null && policy.getGraceMinutes() != null) {

            if (att.getCheckIn().isAfter(
                    policy.getShiftStart().plusMinutes(policy.getGraceMinutes()))) {

                att.setIsLate(true);
            } else {
                att.setIsLate(false);
            }
        }

        // 🔥 EARLY EXIT
        if (policy.getShiftEnd() != null) {

            if (att.getCheckOut().isBefore(policy.getShiftEnd())) {
                att.setIsEarlyExit(true);
            } else {
                att.setIsEarlyExit(false);
            }
        }
    }
}
