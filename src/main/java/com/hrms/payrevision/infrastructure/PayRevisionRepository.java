package com.hrms.payrevision.infrastructure;

import com.hrms.payrevision.domain.PayRevisionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayRevisionRepository extends JpaRepository<PayRevisionRecord, Long> {

    Page<PayRevisionRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT p FROM PayRevisionRecord p
        WHERE p.isDeleted = false AND (
            LOWER(p.payRevisionOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(p.reason.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(p.remarks) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<PayRevisionRecord> searchByOrderNumberOrReasonOrRemarks(
            @Param("search") String search, Pageable pageable);

    Page<PayRevisionRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<PayRevisionRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT p FROM PayRevisionRecord p
        WHERE p.isDeleted = false AND p.isActive = :isActive AND (
            LOWER(p.payRevisionOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(p.reason.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(p.remarks) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<PayRevisionRecord> searchByOrderNumberOrReasonOrRemarksAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<PayRevisionRecord> findByEmployee_IdAndIsDeletedFalseOrderByEffectiveDateDesc(Long empId);
    List<PayRevisionRecord> findByEmployee_IdAndIsDeletedFalse(Long employeeId);
    List<PayRevisionRecord> findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(Long employeeId);

}