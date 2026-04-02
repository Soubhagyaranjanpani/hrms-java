package com.hrms.attendance.infrastructure;

import com.hrms.attendance.domain.Attendance;
import com.hrms.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);

    List<Attendance> findByEmployee(Employee employee);

    List<Attendance> findByEmployeeAndDateBetween(Employee emp, LocalDate start, LocalDate end);

    List<Attendance> findByDate(LocalDate now);
}
