package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.dto.AttendanceResponse;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyAttendanceUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository empRepo;

    public List<AttendanceResponse> execute(String email) {

        Employee emp = empRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return attendanceRepo.findByEmployee(emp)
                .stream()
                .map(this::map)
                .toList();
    }

    private AttendanceResponse map(Attendance a) {

        AttendanceResponse r = new AttendanceResponse();
        r.setId(a.getId());
        r.setEmployeeName(a.getEmployee().getFirstName());
        r.setDate(a.getDate());
        r.setCheckIn(a.getCheckIn());
        r.setCheckOut(a.getCheckOut());
        r.setWorkingHours(a.getWorkingHours());
        r.setOvertimeHours(a.getOvertimeHours());
        r.setStatus(a.getStatus().name());

        return r;
    }
}
