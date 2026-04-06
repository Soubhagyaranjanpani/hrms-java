package com.hrms.leave.application;

import com.hrms.audit.application.AuditLogService;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.leave.domain.*;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.dto.LeaveApprovalRequest;
import com.hrms.leave.engine.ApproverResolver;
import com.hrms.leave.infrastructure.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApproveLeaveUseCase {

    private final LeaveRepository leaveRepo;
    private final EmployeeRepository employeeRepo;
    private final LeavePolicyRepository policyRepo;
    private final LeaveApprovalHistoryRepository historyRepo;
    private final LeaveBalanceRepository balanceRepo;
    private final LeaveApprovalConfigRepository configRepo;
    private final AttendanceRepository attendanceRepo;
    private final ApproverResolver approverResolver;
    private final AuditLogService auditLogService;

    @Transactional
    public String execute(LeaveApprovalRequest request, Principal principal) {

        // 🔥 1. Fetch Leave
        Leave leave = leaveRepo.findById(request.getLeaveId())
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        LeaveStatus oldStatus = leave.getStatus();

        // 🔥 2. Fetch Approver (from token)
        Employee approver = employeeRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        // 🔥 3. Validate status (multi-level safe)
        if (!(LeaveStatus.PENDING.equals(leave.getStatus())
                || LeaveStatus.PENDING_L2.equals(leave.getStatus()))) {
            throw new RuntimeException("Leave already processed");
        }

        // 🔥 4. Authorization check
        if (leave.getCurrentApprover() != null &&
                !leave.getCurrentApprover().getId().equals(approver.getId())) {
            throw new RuntimeException("Not authorized to approve");
        }

        // 🔥 5. Fetch approval configs
        List<LeaveApprovalConfig> configs =
                configRepo.findByLeaveTypeAndIsActiveTrueOrderByLevelAsc(leave.getLeaveType());

        if (configs.isEmpty()) {
            throw new RuntimeException("Approval config not defined");
        }

        int currentLevel = leave.getCurrentApprovalLevel();

        LeaveApprovalConfig currentConfig = configs.stream()
                .filter(c -> c.getLevel().equals(currentLevel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid approval level config"));

        // 🔥 6. Prevent duplicate approval
        boolean alreadyApproved = historyRepo.existsByLeaveAndApproverAndLevel(
                leave, approver, currentLevel);

        if (alreadyApproved) {
            throw new RuntimeException("Already approved at this level");
        }

        // 🔥 7. Save approval history
        LeaveApprovalHistory history = new LeaveApprovalHistory();
        history.setLeave(leave);
        history.setApprover(approver);
        history.setLevel(currentLevel);
        history.setStatus(LeaveStatus.APPROVED);
        history.setRemarks(request.getRemarks());
        history.setActionAt(LocalDateTime.now());

        historyRepo.save(history);

        // 🔥 8. Determine next level
        int nextLevel = currentLevel + 1;

        LeaveApprovalConfig nextConfig = configs.stream()
                .filter(c -> c.getLevel().equals(nextLevel))
                .findFirst()
                .orElse(null);

        if (nextConfig != null) {

            // 🔥 Resolve next approver
            Employee nextApprover =
                    approverResolver.resolveNextApprover(leave, nextConfig);

            if (nextApprover == null) {
                throw new RuntimeException("Next approver not found for level " + nextLevel);
            }

            // 🔥 Move to next level
            leave.setCurrentApprovalLevel(nextLevel);
            leave.setCurrentApprover(nextApprover);
            leave.setStatus(LeaveStatus.PENDING_L2); // 🔥 IMPORTANT

        } else {

            // ✅ FINAL APPROVAL
            leave.setStatus(LeaveStatus.APPROVED);
            leave.setCurrentApprover(null);

            // 🔥 Deduct balance (LOCKED)
            LeaveBalance balance = balanceRepo.findForUpdate(
                    leave.getEmployee(),
                    leave.getLeaveType(),
                    leave.getStartDate().getYear()
            ).orElseThrow(() -> new RuntimeException("Leave balance not found"));

            if (balance.getRemaining() < leave.getTotalDays()) {
                throw new RuntimeException("Insufficient balance at approval time");
            }

            balance.setUsed(balance.getUsed() + leave.getTotalDays());
            balance.setRemaining(balance.getRemaining() - leave.getTotalDays());

            balanceRepo.save(balance);

            // 🔥 MARK ATTENDANCE
            LocalDate date = leave.getStartDate();

            while (!date.isAfter(leave.getEndDate())) {

                Attendance att = attendanceRepo
                        .findByEmployeeAndDate(leave.getEmployee(), date)
                        .orElse(new Attendance());

                att.setEmployee(leave.getEmployee());
                att.setDate(date);
                att.setStatus(AttendanceStatus.LEAVE);
                att.setIsManualEntry(true);

                attendanceRepo.save(att);

                date = date.plusDays(1);
            }
        }

        // 🔥 9. Save leave
        leaveRepo.save(leave);

        // 🔥 10. Audit
        auditLogService.log(
                "LEAVE",
                leave.getId(),
                "LEAVE_APPROVED_LEVEL_" + currentLevel,
                principal.getName(),
                oldStatus,
                leave.getStatus()
        );

        return "Leave approved successfully";
    }
}
