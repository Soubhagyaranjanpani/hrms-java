package com.hrms.deputation.infrastructure;

import com.hrms.deputation.domain.DeputationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeputationRepository extends JpaRepository<DeputationRecord, Long> {

    Page<DeputationRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT d FROM DeputationRecord d
        WHERE d.isDeleted = false AND (
            LOWER(d.deputationOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.deputationOrganization) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.deputationType) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<DeputationRecord> searchByOrderNumberOrOrganizationOrType(
            @Param("search") String search, Pageable pageable);

    // ── Active/Inactive filtering (mirrors Promotion/Appointment/Confirmation/Transfer "flag" endpoints) ──
    Page<DeputationRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<DeputationRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT d FROM DeputationRecord d
        WHERE d.isDeleted = false AND d.isActive = :isActive AND (
            LOWER(d.deputationOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.deputationOrganization) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.deputationType) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<DeputationRecord> searchByOrderNumberOrOrganizationOrTypeAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<DeputationRecord> findByEmployee_IdAndIsDeletedFalseOrderByStartDateDesc(Long empId);
}
