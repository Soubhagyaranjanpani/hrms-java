//package com.hrms.attendance.application;
//
//import com.hrms.attendance.domain.Attendance;
//import com.hrms.attendance.domain.AttendanceStatus;
//import com.hrms.attendance.infrastructure.AttendanceRepository;
//import com.hrms.employee.domain.Employee;
//import com.hrms.employee.infrastructure.EmployeeRepository;
//import com.hrms.leave.infrastructure.LeaveRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class AutoAbsentJob {
//
//    private final AttendanceRepository attendanceRepo;
//    private final EmployeeRepository employeeRepo;
//    private final LeaveRepository leaveRepo;
//
//    @Scheduled(cron = "#{@attendanceCronConfig.autoAbsent}")
//    public void run() {
//
//        LocalDate today = LocalDate.now();
//
//        List<Employee> employees = employeeRepo.findAll();
//
//        for (Employee emp : employees) {
//
//            boolean hasAttendance =
//                    attendanceRepo.findByEmployeeAndDate(emp, today).isPresent();
//
//            boolean hasLeave =
//                    leaveRepo.existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
//                            emp, today, today
//                    );
//
//            if (!hasAttendance && !hasLeave) {
//
//                Attendance att = new Attendance();
//                att.setEmployee(emp);
//                att.setDate(today);
//                att.setStatus(AttendanceStatus.ABSENT);
//
//                attendanceRepo.save(att);
//            }
//        }
//    }
//}
