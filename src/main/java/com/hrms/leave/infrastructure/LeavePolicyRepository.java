package com.hrms.leave.infrastructure;

import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {

    Optional<LeavePolicy> findByLeaveType(LeaveType leaveType);
    @Modifying
    @Query("update LeavePolicy p set p.isActive = false")
    void deactivateAll();

    @Modifying
    @Query("update LeavePolicy p set p.isActive = true where p.id = :id")
    void activateById(Long id);

    @Modifying
    @Query("update LeavePolicy p set p.isActive = false where p.leaveType.id = :leaveTypeId")
    void deactivateByLeaveType(Long leaveTypeId);

    List<LeavePolicy> findByIsActiveTrue();

    Optional<LeavePolicy> findByLeaveTypeAndIsActiveTrue(LeaveType leaveType);
}
