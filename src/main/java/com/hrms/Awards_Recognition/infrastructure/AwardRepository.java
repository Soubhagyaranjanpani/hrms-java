package com.hrms.Awards_Recognition.infrastructure;


import com.hrms.Awards_Recognition.domain.AwardRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AwardRepository extends JpaRepository<AwardRecord, Long> {

    Page<AwardRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT a FROM AwardRecord a
        WHERE a.isDeleted = false AND (
            LOWER(a.awardName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(a.employee.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(a.employee.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(a.awardType.name) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<AwardRecord> searchByAwardNameOrEmployeeOrType(
            @Param("search") String search, Pageable pageable);

    Page<AwardRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<AwardRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT a FROM AwardRecord a
        WHERE a.isDeleted = false AND a.isActive = :isActive AND (
            LOWER(a.awardName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(a.employee.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(a.employee.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(a.awardType.name) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<AwardRecord> searchByAwardNameOrEmployeeOrTypeAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<AwardRecord> findByEmployee_IdAndIsDeletedFalseOrderByAwardDateDesc(Long empId);
}