package com.hrms.retirement.infrastructure;

import com.hrms.retirement.domain.RetirementRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RetirementRepository extends JpaRepository<RetirementRecord, Long> {

    Page<RetirementRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT r FROM RetirementRecord r
        WHERE r.isDeleted = false AND (
            LOWER(CAST(r.employee AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(r.retirementType AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(r.pensionNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(r.retirementOrder) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<RetirementRecord> searchByEmployeeOrTypeOrPensionOrOrder(
            @Param("search") String search, Pageable pageable);

    Page<RetirementRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<RetirementRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT r FROM RetirementRecord r
        WHERE r.isDeleted = false AND r.isActive = :isActive AND (
            LOWER(CAST(r.employee AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(r.retirementType AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(r.pensionNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(r.retirementOrder) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<RetirementRecord> searchByEmployeeOrTypeOrPensionOrOrderAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<RetirementRecord> findByEmployee_IdAndIsDeletedFalseOrderByRetirementDateDesc(Long empId);
}
