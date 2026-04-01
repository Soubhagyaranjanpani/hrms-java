package com.hrms.leave.application;

import com.hrms.audit.application.AuditLogService;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.*;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.dto.LeaveApplyRequest;
import com.hrms.leave.engine.LeavePolicyEngine;
import com.hrms.leave.infrastructure.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ApplyLeaveUseCase {

    private final EmployeeRepository employeeRepo;
    private final LeaveTypeRepository leaveTypeRepo;
    private final LeaveRepository leaveRepo;
    private final LeaveBalanceRepository balanceRepo;
    private final LeavePolicyRepository policyRepo;
    private final AuditLogService auditLogService;

    private final LeavePolicyEngine policyEngine;

    @Transactional
    public String execute(LeaveApplyRequest request) {

        // 1. Fetch Employee
        Employee emp = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 2. Fetch Leave Type
        LeaveType type = leaveTypeRepo.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        // 3. Fetch Policy
        LeavePolicy policy = policyRepo.findByLeaveType(type)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        // 4. Validate Policy Rules
        policyEngine.validate(request, emp, policy);

        // 5. Check overlapping leave
        boolean exists = leaveRepo.existsByEmployeeAndDateRange(
                emp.getId(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (exists) {
            throw new RuntimeException("Leave already exists for selected dates");
        }

        // 6. Calculate days
        double days = policyEngine.calculateLeaveDays(request, policy);

        // 7. Fetch Balance
        LeaveBalance balance = balanceRepo
                .findByEmployeeAndLeaveTypeAndYear(
                        emp,
                        type,
                        LocalDate.now().getYear()
                ).orElseThrow(() -> new RuntimeException("Leave balance not found"));

        if (balance.getRemaining() < days) {
            throw new RuntimeException("Insufficient leave balance");
        }

        // 8. Save Leave
        Leave leave = new Leave();
        leave.setEmployee(emp);
        leave.setLeaveType(type);
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setIsHalfDay(request.getIsHalfDay());
        leave.setHalfDaySession(request.getHalfDaySession());
        leave.setTotalDays(days);
        leave.setReason(request.getReason());
        leave.setStatus(LeaveStatus.PENDING);

        leaveRepo.save(leave);

        auditLogService.log(
                "LEAVE",
                leave.getId(),
                "LEAVE_APPLIED",
                emp.getEmail(),
                null,
                leave
        );

        return "Leave applied successfully";
    }
}
