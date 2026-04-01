package com.hrms.leave.infrastructure;

import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.LeaveApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApprovalHistoryRepository extends JpaRepository<LeaveApprovalHistory, Long> {

    List<LeaveApprovalHistory> findByLeaveOrderByLevelAsc(Leave leave);
}
