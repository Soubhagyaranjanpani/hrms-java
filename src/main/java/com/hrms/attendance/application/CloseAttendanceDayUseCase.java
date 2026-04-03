package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.engine.AttendancePolicyEngine;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloseAttendanceDayUseCase {

    private final AttendanceRepository attendanceRepo;
    private final AttendancePolicyRepository policyRepo;
    private final AttendancePolicyEngine policyEngine;

    public void execute() {

        log.info("CloseAttendanceDayUseCase started");

        LocalDate today = LocalDate.now();

        AttendancePolicy policy = policyRepo.findByIsActiveTrue().orElse(null);

        if (policy == null) {
            log.warn("No active attendance policy found");
            return;
        }

        List<Attendance> attendances =
                attendanceRepo.findByDateAndIsDeletedFalse(today);

        for (Attendance att : attendances) {

            if (att.getCheckIn() != null && att.getCheckOut() == null) {

                // 🔥 use shift end (better than now)
                att.setCheckOut(policy.getShiftEnd());

                policyEngine.applyPolicy(att, policy);

                att.setUpdatedBy("SYSTEM");

                attendanceRepo.save(att);
            }
        }

        log.info("CloseAttendanceDayUseCase completed");
    }
}
