package com.hrms.leave.infrastructure;

import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {

    Optional<LeavePolicy> findByLeaveType(LeaveType leaveType);
}
