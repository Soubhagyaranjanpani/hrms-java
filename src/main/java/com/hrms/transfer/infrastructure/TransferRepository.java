package com.hrms.transfer.infrastructure;

import com.hrms.transfer.domain.TransferRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRepository extends JpaRepository<TransferRecord, Long> {

    Page<TransferRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT t FROM TransferRecord t
        WHERE t.isDeleted = false AND (
            LOWER(t.transferOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(t.toDepartment AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(t.toBranch AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(t.transferReason) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<TransferRecord> searchByOrderNumberOrDepartmentOrBranchOrReason(
            @Param("search") String search, Pageable pageable);

    // ── Active/Inactive filtering (mirrors Promotion/Appointment/Confirmation "flag" endpoints) ──
    Page<TransferRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<TransferRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT t FROM TransferRecord t
        WHERE t.isDeleted = false AND t.isActive = :isActive AND (
            LOWER(t.transferOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(t.toDepartment AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(t.toBranch AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(t.transferReason) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<TransferRecord> searchByOrderNumberOrDepartmentOrBranchOrReasonAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<TransferRecord> findByEmployee_IdAndIsDeletedFalseOrderByTransferDateDesc(Long empId);
    List<TransferRecord> findByEmployee_IdAndIsDeletedFalse(Long employeeId);

}
