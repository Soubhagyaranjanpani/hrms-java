package com.hrms.leave.application;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.LeaveBalance;
import com.hrms.leave.domain.LeaveType;
import com.hrms.leave.infrastructure.LeaveBalanceRepository;
import com.hrms.leave.infrastructure.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitializeLeaveBalanceUseCase {

    private final LeaveTypeRepository leaveTypeRepo;
    private final LeaveBalanceRepository balanceRepo;

    public void execute(Employee employee) {

        List<LeaveType> leaveTypes = leaveTypeRepo.findByIsActiveTrue();

        for (LeaveType lt : leaveTypes) {

            boolean exists = balanceRepo
                    .existsByEmployeeAndLeaveType(employee, lt);

            if (exists) continue;

            LeaveBalance b = new LeaveBalance();
            b.setEmployee(employee);
            b.setLeaveType(lt);

            b.setTotalAllocated(Double.valueOf(0));
            b.setRemaining(Double.valueOf(0));

            balanceRepo.save(b);
        }
    }
}
