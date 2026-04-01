package com.hrms.leave.infrastructure;

import com.hrms.leave.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByNameAndIsActiveTrue(String name);
}
