package com.hrms.leave.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.dto.LeaveDashboardResponse;
import com.hrms.leave.dto.LeaveResponse;
import com.hrms.leave.dto.LeaveStats;
import com.hrms.leave.infrastructure.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLeaveDashboardUseCase {

    private final EmployeeRepository employeeRepo;
    private final LeaveRepository leaveRepo;


    public LeaveDashboardResponse execute(Principal principal, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        Employee emp = employeeRepo.findByEmail(principal.getName())
                .orElseThrow();

        String role = emp.getRole().getName();

        LeaveDashboardResponse res = new LeaveDashboardResponse();

        // 🔥 MY LEAVES (for all)
        res.setMyLeaves(
                leaveRepo.findMyLeaves(emp, pageable).getContent()
        );

        // 🔥 MANAGER DATA
        if ("MANAGER".equalsIgnoreCase(role)) {

            res.setTeamLeaves(
                    leaveRepo.findByEmployee_Manager(emp,pageable)
                            .stream().map(this::map).toList()
            );

            res.setPendingApprovals(
                    leaveRepo.findByCurrentApproverAndStatus(emp, LeaveStatus.PENDING)
                            .stream().map(this::map).toList()
            );
        }

        // 🔥 HR DATA
        if ("HR".equalsIgnoreCase(role)) {

            List<Leave> all = leaveRepo.findAll();

            res.setAllLeaves(all.stream().map(this::map).toList());

            res.setPendingApprovals(
                    leaveRepo.findByStatus(LeaveStatus.PENDING_L2,pageable)
                            .stream().map(this::map).toList()
            );

            // 🔥 Stats
            LeaveStats stats = new LeaveStats();
            stats.setPending(leaveRepo.countByStatus(LeaveStatus.PENDING));
            stats.setApproved(leaveRepo.countByStatus(LeaveStatus.APPROVED));
            stats.setRejected(leaveRepo.countByStatus(LeaveStatus.REJECTED));

            res.setStats(stats);
        }

        return res;
    }

    private LeaveResponse map(Leave l) {
        LeaveResponse r = new LeaveResponse();
        r.setLeaveId(l.getId());
        r.setEmployeeName(l.getEmployee().getFirstName());
        r.setLeaveType(l.getLeaveType().getName());
        r.setStatus(l.getStatus());
        r.setStartDate(l.getStartDate());
        r.setEndDate(l.getEndDate());
        return r;
    }
}
