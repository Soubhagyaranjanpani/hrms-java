package com.hrms.leave.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.*;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.dto.LeaveApprovalRequest;
import com.hrms.leave.infrastructure.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RejectLeaveUseCase {

    private final LeaveRepository leaveRepo;
    private final EmployeeRepository employeeRepo;
    private final LeaveApprovalHistoryRepository historyRepo;

    @Transactional
    public String execute(LeaveApprovalRequest request, String approverEmail) {

        Leave leave = leaveRepo.findById(request.getLeaveId())
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        Employee approver = employeeRepo.findByEmail(approverEmail)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        if (!leave.getStatus().equals(LeaveStatus.PENDING)) {
            throw new RuntimeException("Already processed");
        }

        LeaveApprovalHistory history = new LeaveApprovalHistory();
        history.setLeave(leave);
        history.setApprover(approver);
        history.setLevel(leave.getCurrentApprovalLevel());
        history.setStatus(LeaveStatus.REJECTED);
        history.setRemarks(request.getRemarks());
        history.setActionAt(LocalDateTime.now());

        historyRepo.save(history);

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setRejectionReason(request.getRemarks());

        leaveRepo.save(leave);

        return "Leave rejected";
    }
}
