package com.hrms.attendance.application;

import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.dto.AttendanceLeaveDashboardResponse;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.Holiday;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.infrastructure.HolidayRepository;
import com.hrms.leave.infrastructure.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GetAttendanceLeaveDashboardUseCase {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository employeeRepo;
    private final LeaveRepository leaveRepo;
    private final HolidayRepository holidayRepo;

    public List<AttendanceLeaveDashboardResponse> execute(
            String email,
            int month,
            int year) {

        // 🔹 STEP 1: Fetch employee
        Employee employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 🔹 STEP 2: Date range
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // 🔹 STEP 3: Fetch data
        List<Attendance> attendanceList =
                attendanceRepo.findByEmployeeAndDateBetween(employee, start, end);

        List<Leave> leaves =
                leaveRepo.findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employee, end, start
                );

        Set<LocalDate> holidays = new HashSet<>(
                holidayRepo.findByDateBetween(start, end)
                        .stream()
                        .map(Holiday::getDate)
                        .toList()
        );

        // 🔹 STEP 4: Map attendance for quick lookup
        Map<LocalDate, Attendance> attendanceMap = new HashMap<>();
        for (Attendance a : attendanceList) {
            attendanceMap.put(a.getDate(), a);
        }

        // 🔹 STEP 5: Build response
        List<AttendanceLeaveDashboardResponse> result = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            LocalDate currentDate = date; // 🔥 fix for lambda

            AttendanceLeaveDashboardResponse res = new AttendanceLeaveDashboardResponse();
            res.setDate(currentDate);

            // 🔥 PRIORITY ORDER
            // Leave > Holiday > Weekend > Attendance > Absent

            Optional<Leave> leaveOpt = leaves.stream()
                    .filter(l -> !currentDate.isBefore(l.getStartDate())
                            && !currentDate.isAfter(l.getEndDate()))
                    .findFirst();

            if (leaveOpt.isPresent()) {
                res.setStatus("LEAVE");
                res.setLeaveType(leaveOpt.get().getLeaveType().getName());
            }

            else if (holidays.contains(currentDate)) {
                res.setStatus("HOLIDAY");
            }

            else if (isWeekend(currentDate)) {
                res.setStatus("WEEKEND");
            }

            else if (attendanceMap.containsKey(currentDate)) {

                Attendance att = attendanceMap.get(currentDate);

                res.setStatus(att.getStatus().name());
                res.setWorkingHours(att.getWorkingHours());
            }

            else {
                res.setStatus("ABSENT");
            }

            result.add(res);
        }

        return result;
    }

    // 🔹 Helper method
    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
