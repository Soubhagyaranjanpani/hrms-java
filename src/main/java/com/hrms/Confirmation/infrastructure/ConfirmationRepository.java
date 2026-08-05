package com.hrms.Confirmation.infrastructure;

import com.hrms.Confirmation.domain.ConfirmationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConfirmationRepository extends JpaRepository<ConfirmationRecord, Long> {

    Page<ConfirmationRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT c FROM ConfirmationRecord c
        WHERE c.isDeleted = false AND (
            LOWER(c.confirmationOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(c.remarks) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(c.confirmedBy AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<ConfirmationRecord> searchByOrderNumberOrConfirmedByOrRemarks(
            @Param("search") String search, Pageable pageable);

    // ── Active/Inactive filtering (mirrors the Promotion/Appointment "flag" endpoints) ──
    Page<ConfirmationRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<ConfirmationRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT c FROM ConfirmationRecord c
        WHERE c.isDeleted = false AND c.isActive = :isActive AND (
            LOWER(c.confirmationOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(c.remarks) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(c.confirmedBy AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<ConfirmationRecord> searchByOrderNumberOrConfirmedByOrRemarksAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<ConfirmationRecord> findByEmployee_IdAndIsDeletedFalseOrderByConfirmationDateDesc(Long empId);
    List<ConfirmationRecord> findByEmployee_IdAndIsDeletedFalse(Long employeeId);

}
