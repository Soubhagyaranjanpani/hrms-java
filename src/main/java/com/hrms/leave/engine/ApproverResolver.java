package com.hrms.leave.engine;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.LeaveApprovalConfig;

public interface ApproverResolver {

    Employee resolveNextApprover(Leave leave, LeaveApprovalConfig config);
}
