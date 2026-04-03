package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.engine.AttendancePolicyEngine;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckOutUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository employeeRepo;
    private final AttendancePolicyRepository policyRepo;
    private final AttendancePolicyEngine policyEngine;

    public ApiResponse<String> execute(String email) {

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
            return new ApiResponse<>("FAILURE", "Check-in not found", null);
        }

        // 🔹 STEP 3: Validate check-in exists
        if (attendance.getCheckIn() == null) {
            return new ApiResponse<>("FAILURE", "Check-in missing", null);
        }

        // 🔹 STEP 4: Prevent duplicate checkout
        if (attendance.getCheckOut() != null) {
            return new ApiResponse<>("FAILURE", "Already checked out", null);
        }

        // 🔹 STEP 5: Set checkout time
        attendance.setCheckOut(LocalTime.now());

        // 🔹 STEP 6: Fetch active policy
        AttendancePolicy policy = policyRepo.findByIsActiveTrue().orElse(null);

        if (policy == null) {
            return new ApiResponse<>("FAILURE", "Attendance policy not configured", null);
        }

        // 🔥 STEP 7: Apply business logic
        policyEngine.applyPolicy(attendance, policy);

        // 🔹 STEP 8: Audit
        attendance.setUpdatedBy(email);

        // 🔹 STEP 9: Save
        attendanceRepo.save(attendance);

        // 🔹 STEP 10: Logging
        log.info("Employee {} checked out at {}", email, attendance.getCheckOut());

        return new ApiResponse<>("SUCCESS", "Checked out successfully", null);
    }
}
