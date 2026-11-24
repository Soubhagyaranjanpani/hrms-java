package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.attendance.domain.ShiftType;
import com.hrms.attendance.engine.AttendancePolicyEngine;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckOutUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository employeeRepo;
    private final AttendancePolicyRepository policyRepo;
    private final AttendancePolicyEngine policyEngine;
    private final ShiftPolicyService shiftPolicyService;

    // Backward-compatible signature
    public ApiResponse<String> execute(String email) {
        return execute(email, null);
    }

    // Shift-aware check-out signature
    public ApiResponse<String> execute(String email, String shiftCode) {

        // 🔹 STEP 1: Fetch employee
        Employee employee = employeeRepo.findByEmail(email).orElse(null);

        if (employee == null || Boolean.TRUE.equals(employee.getIsDeleted())) {
            return new ApiResponse<>("FAILURE", "Employee not found", null);
        }

        // 🔹 STEP 2: Fetch today's attendance
        Attendance attendance = attendanceRepo
                .findByEmployeeAndDate(employee, LocalDate.now())
                .orElse(null);

        if (attendance == null) {
            return new ApiResponse<>("FAILURE", "Check-in not found for today", null);
        }

        // 🔹 STEP 3: Validate check-in exists
        if (attendance.getCheckIn() == null) {
            return new ApiResponse<>("FAILURE", "Check-in record is missing", null);
        }

        // 🔹 STEP 4: Prevent duplicate checkout
        if (attendance.getCheckOut() != null) {
            return new ApiResponse<>("FAILURE", "Already checked out today", null);
        }

        LocalTime now = LocalTime.now();

        // 🔹 STEP 5: Resolve Active Policy safely
        AttendancePolicy policy = policyRepo.findFirstByIsActiveTrueOrderByIdDesc()
                .orElseGet(() -> {
                    List<AttendancePolicy> list = policyRepo.findByIsActiveTrue();
                    return list.isEmpty() ? null : list.get(0);
                });

        // 🔹 STEP 6: Resolve Assigned Shift & End Time
        ShiftType shift = ShiftType.fromCode(shiftCode);
        LocalTime shiftEnd = (shift != null && shift.getShiftEnd() != null)
                ? shift.getShiftEnd()
                : (policy != null && policy.getShiftEnd() != null ? policy.getShiftEnd() : LocalTime.of(18, 0));

        // 🔹 STEP 7: Shift Completion & Half-Day Checkout Validation
        double workedMinutes = Duration.between(attendance.getCheckIn(), now).toMinutes();
        double workedHours = Math.max(0.0, workedMinutes / 60.0);

        double shiftDuration = (shift != null && shift.getDurationHours() != null)
                ? shift.getDurationHours()
                : (policy != null && policy.getFullDayHours() != null ? policy.getFullDayHours() : 8.0);
        double halfDayThreshold = (policy != null && policy.getHalfDayThresholdHours() != null)
                ? policy.getHalfDayThresholdHours()
                : (shiftDuration / 2.0);

        boolean isNightShift = shift != null && shift.isNightShift();
        boolean enforceCheckout = shiftPolicyService == null || shiftPolicyService.isEnforceCheckOutAfterShift();

        // If before shift end AND worked less than half-day threshold, lock checkout
        if (enforceCheckout && !isNightShift && now.isBefore(shiftEnd) && workedHours < halfDayThreshold) {
            long remainingMins = Duration.between(now, shiftEnd).toMinutes() + 1;
            String endStr = shiftEnd.toString().substring(0, 5);
            return new ApiResponse<>(
                    "FAILURE",
                    "Check-out is locked until your shift ends at " + endStr + " (" + remainingMins + " min remaining, or minimum half-day of " + halfDayThreshold + " hrs). Early check-out is restricted by shift policy.",
                    null
            );
        }

        // 🔹 STEP 8: Set checkout time
        attendance.setCheckOut(now);

        // 🔥 STEP 9: Apply business logic, working hours & half-day status
        double hours = Math.round(workedHours * 100.0) / 100.0;
        attendance.setWorkingHours(hours);

        if (policy != null) {
            policyEngine.applyPolicy(attendance, policy);
        }

        // Explicit rule: If checkout is in half time of work, count as HALF_DAY
        double fullThreshold = (policy != null && policy.getFullDayHours() != null) ? policy.getFullDayHours() : (shiftDuration - 0.5);
        if (hours >= halfDayThreshold && hours < fullThreshold) {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
            attendance.setIsEarlyExit(true);
        } else if (hours >= fullThreshold) {
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setIsEarlyExit(false);
        } else {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
            attendance.setIsEarlyExit(true);
        }

        // 🔹 STEP 10: Audit & Save to Database
        attendance.setUpdatedBy(email);
        attendanceRepo.save(attendance);

        // 🔹 STEP 11: Logging
        log.info("Employee {} checked out at {} (Status: {}, Hours: {}, Shift: {})", 
                email, attendance.getCheckOut(), attendance.getStatus(), hours, shift != null ? shift.getName() : "General");

        String statusLabel = attendance.getStatus() == AttendanceStatus.HALF_DAY ? "Half Day" : "Present (Full Day)";
        return new ApiResponse<>(
                "SUCCESS",
                "Checked out successfully at " + now.toString().substring(0, 5) + " (" + hours + " hrs worked). Counted as " + statusLabel + ".",
                null
        );
    }
}
