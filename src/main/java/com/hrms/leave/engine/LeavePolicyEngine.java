package com.hrms.leave.engine;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.dto.LeaveApplyRequest;

public interface LeavePolicyEngine {

    void validate(LeaveApplyRequest request, Employee employee, LeavePolicy policy);

    double calculateLeaveDays(LeaveApplyRequest request, LeavePolicy policy);
}
