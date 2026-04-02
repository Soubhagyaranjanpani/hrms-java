package com.hrms.attendance.infrastructure;

import com.hrms.attendance.domain.AttendancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendancePolicyRepository extends JpaRepository<AttendancePolicy, Long> {

    Optional<AttendancePolicy> findByIsActiveTrue();
}
