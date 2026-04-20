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

    // distinct months with data, most recent first
    @Query("SELECT DISTINCT p.yearMonth FROM PayrollRecord p WHERE p.isDeleted=false ORDER BY p.yearMonth DESC")
    List<String> findDistinctMonths();

    // aggregate stats for one month - ensure all columns are returned even with no data
    @Query(value = """
        SELECT 
            COALESCE(SUM(p.net_salary), 0) as netSalary,
            COALESCE(SUM(p.gross_earnings), 0) as grossEarnings,
            COALESCE(SUM(p.total_deductions), 0) as totalDeductions,
            COALESCE(SUM(p.basic_salary), 0) as basicSalary,
            COALESCE(SUM(p.hra), 0) as hra,
            COALESCE(SUM(p.provident_fund), 0) as pf,
            COALESCE(SUM(p.income_tax), 0) as tax,
            COALESCE(COUNT(p.id), 0) as totalCount,
            COALESCE(SUM(CASE WHEN p.status = 'PROCESSED' THEN 1 ELSE 0 END), 0) as processedCount,
            COALESCE(SUM(CASE WHEN p.status = 'PENDING' THEN 1 ELSE 0 END), 0) as pendingCount,
            COALESCE(SUM(CASE WHEN p.status = 'DRAFT' THEN 1 ELSE 0 END), 0) as draftCount
        FROM payroll_record p 
        WHERE p.year_month = :ym AND p.is_deleted = false
    """, nativeQuery = true)
    Object[] aggregateForMonth(@Param("ym") String ym);

    // last 6 months trend
    @Query(value = """
        SELECT 
            p.year_month as yearMonth,
            p.payroll_month as payrollMonth,
            COALESCE(SUM(p.net_salary), 0) as netSalary,
            COALESCE(SUM(p.gross_earnings), 0) as grossEarnings,
            COALESCE(COUNT(p.id), 0) as headCount
        FROM payroll_record p
        WHERE p.is_deleted = false AND p.year_month >= :fromMonth
        GROUP BY p.year_month, p.payroll_month
        ORDER BY p.year_month ASC
    """, nativeQuery = true)
    List<Object[]> monthlyTrend(@Param("fromMonth") String fromMonth);

    // department breakdown for a month
    @Query(value = """
        SELECT 
            d.name as department,
            COALESCE(SUM(p.net_salary), 0) as totalNet,
            COALESCE(COUNT(p.id), 0) as empCount
        FROM payroll_record p 
        JOIN employee e ON p.employee_id = e.id
        JOIN department d ON e.department_id = d.id
        WHERE p.year_month = :ym AND p.is_deleted = false
        GROUP BY d.id, d.name
        ORDER BY totalNet DESC
    """, nativeQuery = true)
    List<Object[]> deptBreakdown(@Param("ym") String ym);
}