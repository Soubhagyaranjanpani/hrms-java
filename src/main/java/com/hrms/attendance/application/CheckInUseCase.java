package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.attendance.infrastructure.AttendanceRepository;
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

    public String execute(String email) {

        Employee emp = empRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate today = LocalDate.now();

        if (attendanceRepo.findByEmployeeAndDate(emp, today).isPresent()) {
            throw new RuntimeException("Already checked in");
        }

        Attendance att = new Attendance();
        att.setEmployee(emp);
        att.setDate(today);
        att.setCheckIn(LocalTime.now());
        att.setStatus(AttendanceStatus.PRESENT);

        attendanceRepo.save(att);

        return "Checked in successfully";
    }
}
