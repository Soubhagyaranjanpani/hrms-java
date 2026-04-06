package com.hrms.leave.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.LeaveBalance;
import com.hrms.leave.domain.LeaveType;
import com.hrms.leave.infrastructure.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitializeLeaveBalanceForAllEmployeesUseCase {

    private final EmployeeRepository employeeRepo;
    private final LeaveBalanceRepository balanceRepo;

    public void execute(LeaveType leaveType) {

        List<Employee> employees = employeeRepo.findAll(); // filter active later

        for (Employee emp : employees) {

            boolean exists = balanceRepo
                    .existsByEmployeeAndLeaveType(emp, leaveType);

            if (exists) continue;

            LeaveBalance b = new LeaveBalance();
            b.setEmployee(emp);
            b.setLeaveType(leaveType);

            b.setTotalAllocated(Double.valueOf(leaveType.getMaxDaysPerYear()));
            b.setRemaining(Double.valueOf(leaveType.getMaxDaysPerYear()));

            balanceRepo.save(b);
        }
    }
}
