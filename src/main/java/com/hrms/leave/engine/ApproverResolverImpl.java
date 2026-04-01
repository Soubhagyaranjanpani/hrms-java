package com.hrms.leave.engine;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.LeaveApprovalConfig;
import com.hrms.leave.domain.enums.ApprovalRoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApproverResolverImpl implements ApproverResolver {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee resolveNextApprover(Leave leave, LeaveApprovalConfig config) {

        if (config.getRoleType() == ApprovalRoleType.MANAGER) {
            return leave.getEmployee().getManager(); // ensure manager exists
        }

        if (config.getRoleType() == ApprovalRoleType.HR) {
            return employeeRepository.findFirstByRole_Name("HR")
                    .orElseThrow(() -> new RuntimeException("HR not found"));
        }

        if (config.getRoleType() == ApprovalRoleType.ADMIN) {
            return employeeRepository.findFirstByRole_Name("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
        }

        throw new RuntimeException("Invalid approval role");
    }
}
