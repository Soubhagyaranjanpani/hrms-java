package com.hrms.leave.infrastructure;

import com.hrms.leave.domain.LeaveApprovalConfig;
import com.hrms.leave.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApprovalConfigRepository extends JpaRepository<LeaveApprovalConfig, Long> {

    List<LeaveApprovalConfig> findByLeaveTypeAndIsActiveTrueOrderByLevelAsc(LeaveType leaveType);
}
