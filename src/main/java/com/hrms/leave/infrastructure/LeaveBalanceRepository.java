package com.hrms.leave.infrastructure;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.LeaveBalance;
import com.hrms.leave.domain.LeaveType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    // 🔥 Get balance (used in apply leave)
    Optional<LeaveBalance> findByEmployeeAndLeaveTypeAndYear(
            Employee employee,
            LeaveType leaveType,
            Integer year
    );

    // 🔥 Lock for concurrency (VERY IMPORTANT)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT lb FROM LeaveBalance lb
        WHERE lb.employee = :employee
        AND lb.leaveType = :leaveType
        AND lb.year = :year
    """)
    Optional<LeaveBalance> findForUpdate(@Param("employee") Employee employee,
                                         @Param("leaveType") LeaveType leaveType,
                                         @Param("year") Integer year);

    // Used in accrual job
    List<LeaveBalance> findByLeaveType(LeaveType leaveType);

    // Fetch all balances for employee
    List<LeaveBalance> findByEmployee(Employee employee);
}
