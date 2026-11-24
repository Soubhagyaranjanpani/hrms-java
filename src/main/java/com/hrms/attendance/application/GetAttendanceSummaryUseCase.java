package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.attendance.dto.AttendanceSummaryResponse;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.infrastructure.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAttendanceSummaryUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository empRepo;
    private final LeaveRepository leaveRepo;

    public AttendanceSummaryResponse execute(String email, int month, int year) {

        Employee emp = empRepo.findByEmail(email).orElse(null);

        if (emp == null || Boolean.TRUE.equals(emp.getIsDeleted())) {
            throw new RuntimeException("Employee not found");
        }

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> list = attendanceRepo
                .findByEmployeeAndDateBetweenAndIsDeletedFalse(emp, start, end);

        AttendanceSummaryResponse res = new AttendanceSummaryResponse();

        for (Attendance a : list) {

            if (a.getStatus() == null) continue;

            switch (a.getStatus()) {
                case PRESENT -> res.setPresentDays(res.getPresentDays() + 1);
                case ABSENT -> res.setAbsentDays(res.getAbsentDays() + 1);
                case LEAVE -> res.setLeaveDays(res.getLeaveDays() + 1);
                case HALF_DAY -> res.setHalfDays(res.getHalfDays() + 1);
                default -> {}
            }

            if (a.getWorkingHours() != null) {
                res.setTotalWorkingHours(
                        res.getTotalWorkingHours() + a.getWorkingHours()
                );
            }
        }

        // Count approved leaves in summary if not already logged in attendance table
        try {
            List<Leave> leaves = leaveRepo.findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(emp, end, start);
            int approvedLeaveDays = 0;
            for (Leave l : leaves) {
                if (l.getStatus() == LeaveStatus.APPROVED && l.getStartDate() != null && l.getEndDate() != null) {
                    LocalDate lStart = l.getStartDate().isBefore(start) ? start : l.getStartDate();
                    LocalDate lEnd = l.getEndDate().isAfter(end) ? end : l.getEndDate();
                    if (!lStart.isAfter(lEnd)) {
                        approvedLeaveDays += (int) java.time.temporal.ChronoUnit.DAYS.between(lStart, lEnd) + 1;
                    }
                }
            }
            if (approvedLeaveDays > res.getLeaveDays()) {
                res.setLeaveDays(approvedLeaveDays);
            }
        } catch (Exception ignored) {}

        return res;
    }
}
