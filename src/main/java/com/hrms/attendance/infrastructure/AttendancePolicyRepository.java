package com.hrms.attendance.infrastructure;

import com.hrms.attendance.domain.AttendancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AttendancePolicyRepository extends JpaRepository<AttendancePolicy, Long> {

    // Change this to return List instead of Optional
    List<AttendancePolicy> findByIsActiveTrue();

    // Add this method to get single active policy safely
    @Query("SELECT p FROM AttendancePolicy p WHERE p.isActive = true ORDER BY p.id DESC LIMIT 1")
    Optional<AttendancePolicy> findFirstByIsActiveTrueOrderByIdDesc();
}