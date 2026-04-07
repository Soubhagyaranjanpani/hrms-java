package com.hrms.leave.infrastructure;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.dto.LeaveResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    List<Leave> findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Employee employee,
            LocalDate endDate,
            LocalDate startDate
    );

    // 🔥 For attendance integration
    boolean existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Employee employee,
            LocalDate endDate,
            LocalDate startDate
    );

    List<Leave> findByCurrentApproverAndStatus(Employee approver, LeaveStatus status);

    List<Leave> findByStatus(LeaveStatus status);

    long countByStatus(LeaveStatus status);
    Page<Leave> findByEmployee(Employee emp, Pageable pageable);

    Page<Leave> findByEmployee_Manager(Employee manager, Pageable pageable);

    Page<Leave> findByStatus(LeaveStatus status, Pageable pageable);

    @Query("""
SELECT new com.hrms.leave.dto.LeaveResponse(
    l.id,
    e.firstName,
    lt.name,
    l.startDate,
    l.endDate,
    l.totalDays,
    l.status
)
FROM Leave l
JOIN l.employee e
JOIN l.leaveType lt
WHERE e = :emp
""")
    Page<LeaveResponse> findMyLeaves(Employee emp, Pageable pageable);
}
