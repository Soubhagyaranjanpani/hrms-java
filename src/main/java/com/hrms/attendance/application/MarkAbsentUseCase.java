package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkAbsentUseCase {

    private final EmployeeRepository employeeRepo;
    private final AttendanceRepository attendanceRepo;

    public void execute() {

        log.info("MarkAbsentUseCase started");

        LocalDate today = LocalDate.now();

        List<Employee> employees = employeeRepo.findAll();

        for (Employee emp : employees) {

            if (Boolean.TRUE.equals(emp.getIsDeleted()) || !Boolean.TRUE.equals(emp.getIsActive())) {
                continue;
            }

            boolean exists = attendanceRepo
                    .findByEmployeeAndDate(emp, today)
                    .isPresent();

            if (!exists) {
                Attendance att = new Attendance();
                att.setEmployee(emp);
                att.setDate(today);
                att.setStatus(AttendanceStatus.ABSENT);
                att.setCreatedBy("SYSTEM");

                attendanceRepo.save(att);
            }
        }

        log.info("MarkAbsentUseCase completed");
    }
}
