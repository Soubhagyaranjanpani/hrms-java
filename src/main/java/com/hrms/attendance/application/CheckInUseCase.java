package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class CheckInUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository empRepo;

    public ApiResponse<String> execute(String email) {

        Employee emp = empRepo.findByEmail(email).orElse(null);

        if (emp == null || Boolean.TRUE.equals(emp.getIsDeleted())) {
            return new ApiResponse<>("FAILURE", "Employee not found", null);
        }

        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepo
                .findByEmployeeAndDate(emp, today)
                .orElse(null);

        if (attendance != null) {

            if (attendance.getCheckIn() != null) {
                return new ApiResponse<>("FAILURE", "Already checked in today", null);
            }

            attendance.setCheckIn(LocalTime.now());
            attendanceRepo.save(attendance);

            return new ApiResponse<>("SUCCESS", "Checked in successfully", null);
        }

        Attendance att = new Attendance();
        att.setEmployee(emp);
        att.setDate(today);
        att.setCheckIn(LocalTime.now());

        attendanceRepo.save(att);

        return new ApiResponse<>("SUCCESS", "Checked in successfully", null);
    }
}
