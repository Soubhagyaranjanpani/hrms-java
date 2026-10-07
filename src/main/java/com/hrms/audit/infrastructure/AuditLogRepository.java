package com.hrms.audit.repository;

import com.hrms.audit.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>,
        JpaSpecificationExecutor<AuditLog> {

    // Custom query methods
    Page<AuditLog> findByEmployeeNameContainingIgnoreCaseOrEmployeeCodeContainingIgnoreCase(
            String employeeName, String employeeCode, Pageable pageable);

    Page<AuditLog> findByModule(String module, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);

    Page<AuditLog> findByPerformedByContainingIgnoreCase(String performedBy, Pageable pageable);

    Page<AuditLog> findByEventTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("SELECT DISTINCT a.module FROM AuditLog a")
    List<String> findDistinctModules();

    @Query("SELECT DISTINCT a.action FROM AuditLog a")
    List<String> findDistinctActions();
}