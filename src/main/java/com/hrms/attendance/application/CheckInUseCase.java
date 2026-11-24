package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.attendance.domain.ShiftType;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository empRepo;
    private final AttendancePolicyRepository policyRepo;
    private final ShiftPolicyService shiftPolicyService;

    // Backward-compatible signature
    public ApiResponse<String> execute(String email) {
        return execute(email, null);
    }

    // Shift-aware check-in signature
    public ApiResponse<String> execute(String email, String shiftCode) {

        Employee emp = empRepo.findByEmail(email).orElse(null);

        if (emp == null || Boolean.TRUE.equals(emp.getIsDeleted())) {
            return new ApiResponse<>("FAILURE", "Employee not found", null);
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 1. Resolve Active Attendance Policy
        AttendancePolicy policy = policyRepo.findFirstByIsActiveTrueOrderByIdDesc()
                .orElseGet(() -> {
                    List<AttendancePolicy> activePolicies = policyRepo.findByIsActiveTrue();
                    return activePolicies.isEmpty() ? null : activePolicies.get(0);
                });

        // 2. Resolve Assigned Shift (or Default)
        ShiftType shift = ShiftType.fromCode(shiftCode);
        LocalTime shiftStart = (shift != null && shift.getShiftStart() != null)
                ? shift.getShiftStart()
                : (policy != null && policy.getShiftStart() != null ? policy.getShiftStart() : LocalTime.of(9, 0));

        // 3. Strict 5-Minute Pre-Shift Check-In Window Enforcement
        int checkInBeforeMins = shiftPolicyService != null ? shiftPolicyService.getCheckInBeforeMinutes() : 30;
        LocalTime checkInAllowedTime = shiftStart.minusMinutes(checkInBeforeMins);

        if (now.isBefore(checkInAllowedTime)) {
            long remainingMins = Duration.between(now, checkInAllowedTime).toMinutes() + 1;
            String allowedStr = checkInAllowedTime.toString().substring(0, 5);
            return new ApiResponse<>(
                    "FAILURE",
                    "Check-in opens " + checkInBeforeMins + " minutes before your shift start at " + allowedStr + " (" + remainingMins + " min remaining). Early check-in is not permitted.",
                    null
            );
        }

        // 4. Check for Existing Today Record
        Attendance attendance = attendanceRepo
                .findByEmployeeAndDate(emp, today)
                .orElse(null);

        if (attendance != null) {
            if (attendance.getCheckIn() != null) {
                return new ApiResponse<>("FAILURE", "Already checked in today", null);
            }

            // Update check-in and recalculate status
            attendance.setCheckIn(now);
            updateAttendanceStatus(attendance, shift, policy);
            attendanceRepo.save(attendance);

            String shiftLabel = shift != null ? shift.getName() : "General Shift";
            return new ApiResponse<>("SUCCESS", "Checked in successfully at " + now.toString().substring(0, 5) + " for " + shiftLabel, null);
        }

        // 5. Create New Attendance Record & Persist to Database
        Attendance att = new Attendance();
        att.setEmployee(emp);
        att.setDate(today);
        att.setCheckIn(now);
        att.setCreatedBy(email);

        updateAttendanceStatus(att, shift, policy);

        attendanceRepo.save(att);

        String shiftLabel = shift != null ? shift.getName() : "General Shift";
        return new ApiResponse<>("SUCCESS", "Checked in successfully at " + now.toString().substring(0, 5) + " for " + shiftLabel, null);
    }

    private void updateAttendanceStatus(Attendance attendance, ShiftType shift, AttendancePolicy policy) {
        LocalTime checkInTime = attendance.getCheckIn();

        if (checkInTime == null) {
            attendance.setStatus(AttendanceStatus.ABSENT);
            return;
        }

        LocalTime shiftStart = (shift != null && shift.getShiftStart() != null)
                ? shift.getShiftStart()
                : (policy != null && policy.getShiftStart() != null ? policy.getShiftStart() : LocalTime.of(9, 0));

        Integer graceMinutes = (shift != null && shift.getGraceMinutes() != null)
                ? shift.getGraceMinutes()
                : (policy != null && policy.getGraceMinutes() != null ? policy.getGraceMinutes() : 15);

        Integer halfDayThresholdHours = (policy != null && policy.getHalfDayThresholdHours() != null)
                ? policy.getHalfDayThresholdHours()
                : 4;

        LocalTime graceEndTime = shiftStart.plusMinutes(graceMinutes);
        LocalTime halfDayThreshold = shiftStart.plusHours(halfDayThresholdHours);

        // Before shift start - ON TIME (PRESENT)
        if (!checkInTime.isAfter(shiftStart)) {
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setIsLate(false);
        }
        // Within grace period - PRESENT (with LATE flag)
        else if (!checkInTime.isAfter(graceEndTime)) {
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setIsLate(true);
        }
        // After grace period but before half-day threshold - HALF DAY
        else if (checkInTime.isBefore(halfDayThreshold)) {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
            attendance.setIsLate(true);
        }
        // After half-day threshold - HALF DAY
        else {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
            attendance.setIsLate(true);
        }
    }
}
