package com.hrms.leave.infrastructure;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.LeaveApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApprovalHistoryRepository extends JpaRepository<LeaveApprovalHistory, Long> {

    boolean existsByLeaveAndApproverAndLevel(
            Leave leave,
            Employee approver,
            Integer level
    );
}
