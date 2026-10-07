package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository empRepo;
    private final AttendancePolicyRepository policyRepo;

    public ApiResponse<String> execute(String email) {

        Employee emp = empRepo.findByEmail(email).orElse(null);

        if (emp == null || Boolean.TRUE.equals(emp.getIsDeleted())) {
            return new ApiResponse<>("FAILURE", "Employee not found", null);
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Get active policies
        List<AttendancePolicy> activePolicies = policyRepo.findByIsActiveTrue();
        if (activePolicies.isEmpty()) {
            return new ApiResponse<>("FAILURE", "No active attendance policy found", null);
        }

        // Use the first active policy (or you can implement policy selection logic)
        AttendancePolicy policy = activePolicies.get(0);

        Attendance attendance = attendanceRepo
                .findByEmployeeAndDate(emp, today)
                .orElse(null);

        if (attendance != null) {

            if (attendance.getCheckIn() != null) {
                return new ApiResponse<>("FAILURE", "Already checked in today", null);
            }

            // Update check-in and recalculate status
            attendance.setCheckIn(now);
            updateAttendanceStatus(attendance, policy);
            attendanceRepo.save(attendance);

            return new ApiResponse<>("SUCCESS", "Checked in successfully", null);
        }

        // Create new attendance record
        Attendance att = new Attendance();
        att.setEmployee(emp);
        att.setDate(today);
        att.setCheckIn(now);

        // Set status based on check-in time and policy
        updateAttendanceStatus(att, policy);

        attendanceRepo.save(att);

        return new ApiResponse<>("SUCCESS", "Checked in successfully", null);
    }

    private void updateAttendanceStatus(Attendance attendance, AttendancePolicy policy) {
        LocalTime checkInTime = attendance.getCheckIn();

        if (checkInTime == null) {
            attendance.setStatus(AttendanceStatus.ABSENT);
            return;
        }

        LocalTime shiftStart = policy.getShiftStart();
        Integer graceMinutes = policy.getGraceMinutes();
        Integer halfDayThresholdHours = policy.getHalfDayThresholdHours();

        LocalTime graceEndTime = shiftStart.plusMinutes(graceMinutes);
        LocalTime halfDayThreshold = shiftStart.plusHours(halfDayThresholdHours);

        // Before shift start - EARLY/ON TIME (mark as PRESENT)
        if (checkInTime.isBefore(shiftStart)) {
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setIsLate(false);
        }
        // Within grace period - PRESENT but with LATE flag
        else if (!checkInTime.isAfter(graceEndTime)) {
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setIsLate(true);
        }
        // After grace period but before half-day threshold - LATE
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