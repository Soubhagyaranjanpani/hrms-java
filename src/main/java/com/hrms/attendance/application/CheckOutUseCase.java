package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.engine.AttendancePolicyEngine;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import com.hrms.attendance.infrastructure.AttendanceRepository;
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

    public String execute(String email) {

        // 🔹 STEP 1: Fetch employee
        Employee employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 🔹 STEP 2: Fetch today's attendance
        Attendance attendance = attendanceRepo
                .findByEmployeeAndDate(employee, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Check-in not found"));

        // 🔹 STEP 3: Prevent duplicate checkout
        if (attendance.getCheckOut() != null) {
            throw new RuntimeException("Already checked out");
        }

        // 🔹 STEP 4: Set checkout time
        attendance.setCheckOut(LocalTime.now());

        // 🔹 STEP 5: Fetch active attendance policy
        AttendancePolicy policy = policyRepo.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Attendance policy not configured"));

        // 🔥 STEP 6: Apply policy (core business logic)
        policyEngine.applyPolicy(attendance, policy);

        // 🔹 STEP 7: Audit fields
        attendance.setUpdatedBy(email);

        // 🔹 STEP 8: Save attendance
        attendanceRepo.save(attendance);

        // 🔹 STEP 9: Logging (optional but recommended)
        log.info("Employee {} checked out successfully at {}", email, attendance.getCheckOut());

        return "Checked out successfully";
    }
}
