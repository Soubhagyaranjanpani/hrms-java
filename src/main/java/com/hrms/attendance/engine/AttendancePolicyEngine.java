package com.hrms.attendance.engine;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.domain.AttendanceStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AttendancePolicyEngine {

    public void applyPolicy(Attendance att, AttendancePolicy policy) {

        if (att.getCheckIn() == null || att.getCheckOut() == null) return;

        double hours = Duration.between(
                att.getCheckIn(),
                att.getCheckOut()
        ).toMinutes() / 60.0;

        att.setWorkingHours(hours);

        // 🔥 Half day
        if (hours < policy.getHalfDayThresholdHours()) {
            att.setStatus(AttendanceStatus.HALF_DAY);
        }

        // 🔥 Full day
        if (hours >= policy.getFullDayHours()) {
            att.setStatus(AttendanceStatus.PRESENT);
        }

        // 🔥 Overtime
        if (hours > policy.getFullDayHours()) {
            att.setOvertimeHours(hours - policy.getFullDayHours());
        }

//        // 🔥 Late login
//        if (att.getCheckIn().isAfter(
//                policy.getShiftStart().plusMinutes(policy.getGraceMinutes()))) {
//            att.setIsLate(true);
//        }
//
//        // 🔥 Early exit
//        if (att.getCheckOut().isBefore(policy.getShiftEnd())) {
//            att.setIsEarlyExit(true);
//        }
    }
}
