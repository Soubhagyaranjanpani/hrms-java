package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.attendance.dto.AttendanceSummaryResponse;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAttendanceSummaryUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository empRepo;

    public AttendanceSummaryResponse execute(String email, int month, int year) {

        Employee emp = empRepo.findByEmail(email).orElseThrow();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> list = attendanceRepo.findByEmployeeAndDateBetween(emp, start, end);

        AttendanceSummaryResponse res = new AttendanceSummaryResponse();

        for (Attendance a : list) {

            switch (a.getStatus()) {
                case PRESENT -> res.setPresentDays(res.getPresentDays() + 1);
                case ABSENT -> res.setAbsentDays(res.getAbsentDays() + 1);
                case LEAVE -> res.setLeaveDays(res.getLeaveDays() + 1);
                case HALF_DAY -> res.setHalfDays(res.getHalfDays() + 1);
            }

            if (a.getWorkingHours() != null) {
                res.setTotalWorkingHours(
                        res.getTotalWorkingHours() + a.getWorkingHours()
                );
            }
        }

        return res;
    }
}
