
package com.hrms.EmployeeVerification.infrastructure;

import com.hrms.EmployeeVerification.domain.EmployeeVerificationRequest;
import com.hrms.EmployeeVerification.domain.VerificationStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeVerificationRepository
        extends JpaRepository<EmployeeVerificationRequest, Long> {

    // Employee's own verification requests
    List<EmployeeVerificationRequest>
    findByEmployee_IdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long employeeId
    );

    // HR pending verification list
    List<EmployeeVerificationRequest>
    findByStatusAndIsDeletedFalseOrderByCreatedAtDesc(
            VerificationStatus status
    );

    // HR notification count
    long countByStatusAndIsDeletedFalse(
            VerificationStatus status
    );

    // Lock request while HR approves or rejects
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r
        FROM EmployeeVerificationRequest r
        WHERE r.id = :id
        """)
    Optional<EmployeeVerificationRequest> findLockedById(
            @Param("id") Long id
    );
}
