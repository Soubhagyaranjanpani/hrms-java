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

import java.security.Principal;
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
    public String execute(LeaveApplyRequest request, Principal principal) {

        // 🔥 1. Fetch Employee from TOKEN
        Employee emp = employeeRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 🔥 2. Validate LeaveTypeId
        if (request.getLeaveTypeId() == null) {
            throw new RuntimeException("LeaveTypeId is required");
        }

        // 🔥 3. Fetch Leave Type
        LeaveType type = leaveTypeRepo.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        // 🔥 4. Fetch ACTIVE Policy
        LeavePolicy policy = policyRepo
                .findByLeaveTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new RuntimeException("Active policy not found"));

        // 🔥 5. Validate Policy Rules
        policyEngine.validate(request, emp, policy);

        // 🔥 6. Check overlapping leave
        boolean exists = leaveRepo.existsByEmployeeAndDateRange(
                emp.getId(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (exists) {
            throw new RuntimeException("Leave already exists for selected dates");
        }

        // 🔥 7. Calculate leave days
        double days = policyEngine.calculateLeaveDays(request, policy);

        // 🔥 8. Fetch or CREATE balance (SELF HEALING)
        LeaveBalance balance = balanceRepo
                .findByEmployeeAndLeaveTypeAndYear(
                        emp,
                        type,
                        LocalDate.now().getYear()
                )
                .orElseGet(() -> {
                    LeaveBalance b = new LeaveBalance();
                    b.setEmployee(emp);
                    b.setLeaveType(type);
                    b.setYear(LocalDate.now().getYear());
                    b.setTotalAllocated(0.0);
                    b.setRemaining(0.0);
                    return balanceRepo.save(b);
                });

        // 🔥 9. Balance validation
        if (balance.getRemaining() < days) {
            throw new RuntimeException("Insufficient leave balance");
        }

        // 🔥 10. Save Leave
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

        // 🔥 11. Audit
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
