package com.hrms.leave.infrastructure;

import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // 🔥 Overlapping leave check
    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM Leave l
        WHERE l.employee.id = :employeeId
        AND l.isDeleted = false
        AND (
            (:startDate BETWEEN l.startDate AND l.endDate)
            OR (:endDate BETWEEN l.startDate AND l.endDate)
            OR (l.startDate BETWEEN :startDate AND :endDate)
        )
    """)
    boolean existsByEmployeeAndDateRange(Long employeeId,
                                         LocalDate startDate,
                                         LocalDate endDate);

    // Fetch leaves of employee
    List<Leave> findByEmployeeIdAndIsDeletedFalse(Long employeeId);

    // Fetch by status
    List<Leave> findByStatusAndIsDeletedFalse(LeaveStatus status);

    // Date range query
    List<Leave> findByStartDateBetweenAndIsDeletedFalse(LocalDate start, LocalDate end);

}
