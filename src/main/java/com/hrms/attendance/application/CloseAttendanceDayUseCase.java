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

        // FIXED: Handle List instead of Optional
        List<AttendancePolicy> activePolicies = policyRepo.findByIsActiveTrue();
        AttendancePolicy policy = activePolicies.isEmpty() ? null : activePolicies.get(0);

        if (policy == null) {
            log.warn("No active attendance policy found");
            return;
        }

        // If multiple active policies exist, log warning and use the first one
        if (activePolicies.size() > 1) {
            log.warn("Found {} active policies. Using policy ID: {}",
                    activePolicies.size(), policy.getId());
        }

        List<Attendance> attendances = attendanceRepo.findByDateAndIsDeletedFalse(today);

        for (Attendance att : attendances) {
            if (att.getCheckIn() != null && att.getCheckOut() == null) {
                // Use shift end time from policy
                att.setCheckOut(policy.getShiftEnd());

                policyEngine.applyPolicy(att, policy);

                att.setUpdatedBy("SYSTEM");

                attendanceRepo.save(att);
            }
        }

        log.info("CloseAttendanceDayUseCase completed");
    }
}