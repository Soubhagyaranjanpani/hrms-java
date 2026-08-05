//package com.hrms.promotion.infrastructure;
//
//import com.hrms.promotion.domain.PromotionRecord;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface PromotionRepository extends JpaRepository<PromotionRecord, Long> {
//
//    Page<PromotionRecord> findByIsDeletedFalse(Pageable pageable);
//
//    @Query("SELECT p FROM PromotionRecord p WHERE p.isDeleted = false")
//    List<PromotionRecord> findAllByIsDeletedFalse();
//
//    // 🔥 FIXED: Using STRING conversion for all fields to avoid entity field issues
//    @Query("""
//        SELECT p FROM PromotionRecord p
//        WHERE p.isDeleted = false AND (
//            LOWER(p.promotionOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
//            LOWER(CAST(p.newDesignation AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
//            LOWER(CAST(p.oldDesignation AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
//            LOWER(CAST(p.promotionType AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
//        )
//        """)
//    Page<PromotionRecord> searchByOrderNumberOrDesignationOrType(@Param("search") String search, Pageable pageable);
//
//    List<PromotionRecord> findByEmployee_IdAndIsDeletedFalseOrderByPromotionDateDesc(Long empId);
//
//    List<PromotionRecord> findByPromotionYearAndIsDeletedFalse(String year);
//
//    @Query("SELECT DISTINCT p.promotionYear FROM PromotionRecord p WHERE p.isDeleted=false ORDER BY p.promotionYear DESC")
//    List<String> findDistinctYears();
//
//    @Query(value = """
//        SELECT
//            COALESCE(COUNT(p.id), 0) as totalCount,
//            COALESCE(SUM(CASE WHEN p.is_active = true THEN 1 ELSE 0 END), 0) as activeCount,
//            COALESCE(SUM(CASE WHEN p.is_active = false THEN 1 ELSE 0 END), 0) as inactiveCount,
//            COALESCE(AVG(p.increment_amount), 0) as avgIncrementAmount,
//            COALESCE(AVG(p.increment_percent), 0) as avgIncrementPercent,
//            COALESCE(SUM(p.increment_amount), 0) as totalIncrementAmount
//        FROM promotion_records p
//        WHERE p.promotion_year = :year AND p.is_deleted = false
//    """, nativeQuery = true)
//    Object[] aggregateForYear(@Param("year") String year);
//
//    @Query(value = """
//        SELECT
//            COALESCE(d.name, 'Unassigned') as department,
//            COALESCE(COUNT(p.id), 0) as empCount,
//            COALESCE(AVG(p.increment_amount), 0) as avgIncrement
//        FROM promotion_records p
//        LEFT JOIN employees e ON p.employee_id = e.id
//        LEFT JOIN departments d ON e.department_id = d.id
//        WHERE p.is_deleted = false
//        GROUP BY COALESCE(d.name, 'Unassigned')
//        ORDER BY empCount DESC
//    """, nativeQuery = true)
//    List<Object[]> deptBreakdown();
//
//
//}



package com.hrms.promotion.infrastructure;

import com.hrms.promotion.domain.PromotionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionRepository extends JpaRepository<PromotionRecord, Long> {

    Page<PromotionRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM PromotionRecord p WHERE p.isDeleted = false")
    List<PromotionRecord> findAllByIsDeletedFalse();

    // 🔥 FIXED: Using STRING conversion for all fields to avoid entity field issues
    @Query("""
        SELECT p FROM PromotionRecord p 
        WHERE p.isDeleted = false AND (
            LOWER(p.promotionOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(p.newDesignation AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(p.oldDesignation AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(p.promotionType AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<PromotionRecord> searchByOrderNumberOrDesignationOrType(@Param("search") String search, Pageable pageable);

    // ── NEW METHODS FOR FLAG FILTERING ──────────────────────────
    Page<PromotionRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<PromotionRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT p FROM PromotionRecord p 
        WHERE p.isDeleted = false AND p.isActive = :isActive AND (
            LOWER(p.promotionOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(p.newDesignation AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(p.oldDesignation AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(p.promotionType AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<PromotionRecord> searchByOrderNumberOrDesignationOrTypeAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    // ── EXISTING METHODS ─────────────────────────────────────
    List<PromotionRecord> findByEmployee_IdAndIsDeletedFalseOrderByPromotionDateDesc(Long empId);

    List<PromotionRecord> findByPromotionYearAndIsDeletedFalse(String year);

    @Query("SELECT DISTINCT p.promotionYear FROM PromotionRecord p WHERE p.isDeleted=false ORDER BY p.promotionYear DESC")
    List<String> findDistinctYears();

    @Query(value = """
        SELECT 
            COALESCE(COUNT(p.id), 0) as totalCount,
            COALESCE(SUM(CASE WHEN p.is_active = true THEN 1 ELSE 0 END), 0) as activeCount,
            COALESCE(SUM(CASE WHEN p.is_active = false THEN 1 ELSE 0 END), 0) as inactiveCount,
            COALESCE(AVG(p.increment_amount), 0) as avgIncrementAmount,
            COALESCE(AVG(p.increment_percent), 0) as avgIncrementPercent,
            COALESCE(SUM(p.increment_amount), 0) as totalIncrementAmount
        FROM promotion_records p
        WHERE p.promotion_year = :year AND p.is_deleted = false
    """, nativeQuery = true)
    Object[] aggregateForYear(@Param("year") String year);

    @Query(value = """
        SELECT 
            COALESCE(d.name, 'Unassigned') as department,
            COALESCE(COUNT(p.id), 0) as empCount,
            COALESCE(AVG(p.increment_amount), 0) as avgIncrement
        FROM promotion_records p
        LEFT JOIN employees e ON p.employee_id = e.id
        LEFT JOIN departments d ON e.department_id = d.id
        WHERE p.is_deleted = false
        GROUP BY COALESCE(d.name, 'Unassigned')
        ORDER BY empCount DESC
    """, nativeQuery = true)
    List<Object[]> deptBreakdown();
    List<PromotionRecord> findByEmployee_IdAndIsDeletedFalse(Long employeeId);

}