package com.hrms.leave.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.dto.LeaveResponse;
import com.hrms.leave.infrastructure.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTeamLeavesUseCase {

    private final EmployeeRepository employeeRepo;
    private final LeaveRepository leaveRepo;

    public List<LeaveResponse> execute(Principal principal) {

        Employee manager = employeeRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        List<Employee> team = employeeRepo.findByManager(manager);

        List<Leave> leaves = team.stream()
                .flatMap(emp -> leaveRepo.findByEmployeeIdAndIsDeletedFalse(emp.getId()).stream())
                .collect(Collectors.toList());

        return leaves.stream().map(this::map).collect(Collectors.toList());
    }

    private LeaveResponse map(Leave l) {
        LeaveResponse res = new LeaveResponse();
        res.setLeaveId(l.getId());
        res.setEmployeeName(l.getEmployee().getFirstName());
        res.setLeaveType(l.getLeaveType().getName());
        res.setStartDate(l.getStartDate());
        res.setEndDate(l.getEndDate());
        res.setTotalDays(l.getTotalDays());
        res.setStatus(l.getStatus());
        return res;
    }
}
