package com.hrms.payroll.infrastructure;

import com.hrms.payroll.domain.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<PayrollRecord, Long> {

    List<PayrollRecord> findByYearMonthAndIsDeletedFalse(String yearMonth);

    Optional<PayrollRecord> findByEmployee_IdAndYearMonthAndIsDeletedFalse(Long empId, String ym);

    List<PayrollRecord> findByEmployee_IdAndIsDeletedFalseOrderByYearMonthDesc(Long empId);

    List<PayrollRecord> findByYearMonthAndStatusAndIsDeletedFalse(String ym, String status);

    @Query("SELECT DISTINCT p.yearMonth FROM PayrollRecord p WHERE p.isDeleted=false ORDER BY p.yearMonth DESC")
    List<String> findDistinctMonths();

    // FIXED: Use EXACT same table name that worked for monthlyTrend
    @Query(value = """
        SELECT 
            COALESCE(SUM(p.net_salary), 0) as netSalary,
            COALESCE(SUM(p.gross_earnings), 0) as grossEarnings,
            COALESCE(SUM(p.total_deductions), 0) as totalDeductions,
            COALESCE(SUM(p.basic_salary), 0) as basicSalary,
            COALESCE(SUM(p.hra), 0) as hra,
            COALESCE(SUM(p.provident_fund), 0) as pf,
            COALESCE(SUM(p.professional_tax + p.income_tax), 0) as totalTax,
            COALESCE(COUNT(p.id), 0) as totalCount,
            COALESCE(SUM(CASE WHEN p.status = 'PROCESSED' THEN 1 ELSE 0 END), 0) as processedCount,
            COALESCE(SUM(CASE WHEN p.status = 'PENDING' THEN 1 ELSE 0 END), 0) as pendingCount,
            COALESCE(SUM(CASE WHEN p.status = 'DRAFT' THEN 1 ELSE 0 END), 0) as draftCount
        FROM payroll_records p 
        WHERE p.year_month = :ym AND p.is_deleted = false
    """, nativeQuery = true)
    Object[] aggregateForMonth(@Param("ym") String ym);

    // This query WORKS - use it as reference for table names
    @Query(value = """
        SELECT 
            p.year_month as yearMonth,
            COALESCE(MIN(p.payroll_month), '') as payrollMonth,
            COALESCE(SUM(p.net_salary), 0) as netSalary,
            COALESCE(SUM(p.gross_earnings), 0) as grossEarnings,
            COALESCE(COUNT(p.id), 0) as headCount
        FROM payroll_records p
        WHERE p.is_deleted = false AND p.year_month >= :fromMonth
        GROUP BY p.year_month
        ORDER BY p.year_month ASC
    """, nativeQuery = true)
    List<Object[]> monthlyTrend(@Param("fromMonth") String fromMonth);

    // FIXED: Use same table names that work
    @Query(value = """
        SELECT 
            COALESCE(d.name, 'Unassigned') as department,
            COALESCE(SUM(p.net_salary), 0) as totalNet,
            COALESCE(COUNT(p.id), 0) as empCount
        FROM payroll_records p 
        LEFT JOIN employees e ON p.employee_id = e.id
        LEFT JOIN departments d ON e.department_id = d.id
        WHERE p.year_month = :ym AND p.is_deleted = false
        GROUP BY COALESCE(d.name, 'Unassigned')
        ORDER BY totalNet DESC
    """, nativeQuery = true)
    List<Object[]> deptBreakdown(@Param("ym") String ym);
}