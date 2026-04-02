package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.engine.AttendancePolicyEngine;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoCheckoutJob {

    private final AttendanceRepository attendanceRepo;
    private final AttendancePolicyService policyService;
    private final AttendancePolicyEngine policyEngine;

    @Scheduled(cron = "#{@attendanceCronConfig.autoCheckout}")
    public void run() {

        AttendancePolicy policy = policyService.getActive(); // ✅ FIXED

        List<Attendance> list = attendanceRepo.findByDate(LocalDate.now());

        for (Attendance att : list) {

            if (att.getCheckIn() != null && att.getCheckOut() == null) {

                att.setCheckOut(policy.getShiftEnd());

                policyEngine.applyPolicy(att, policy);

                attendanceRepo.save(att);
            }
        }
    }
}
