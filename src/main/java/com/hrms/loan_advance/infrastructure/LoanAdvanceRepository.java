package com.hrms.loan_advance.infrastructure;

import com.hrms.loan_advance.domain.LoanAdvance;
import com.hrms.loan_advance.domain.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Only LoanAdvanceService uses this repository.
 * Pass an UNSORTED Pageable: the ordering (newest first) is already part of every query.
 */
@Repository
public interface LoanAdvanceRepository extends JpaRepository<LoanAdvance, Long> {

    Page<LoanAdvance> findAllByOrderByLoanIdDesc(Pageable pageable);

    Page<LoanAdvance> findByEmpIdOrderByLoanIdDesc(String empId, Pageable pageable);

    Optional<LoanAdvance> findTopByOrderByLoanIdDesc();

    // Postgres will not compare an enum column with a varchar parameter, so status filters are native
    // queries that cast the column to text first (cast(...), not ::, because Hibernate collapses "::"
    // in native queries). Native queries need an explicit countQuery for paging.

    @Query(value = "select * from loan where cast(status as text) = :label order by loan_id desc",
            countQuery = "select count(*) from loan where cast(status as text) = :label",
            nativeQuery = true)
    Page<LoanAdvance> findByStatusLabel(@Param("label") String label, Pageable pageable);

    @Query(value = "select * from loan where emp_id = :empId and cast(status as text) = :label order by loan_id desc",
            countQuery = "select count(*) from loan where emp_id = :empId and cast(status as text) = :label",
            nativeQuery = true)
    Page<LoanAdvance> findByEmpIdAndStatusLabel(@Param("empId") String empId,
                                                @Param("label") String label,
                                                Pageable pageable);

    /** What the employee still owes on running loans (Approved/Active), ignoring one loan id. */
    @Query(value = "select coalesce(sum(balance), 0) from loan "
            + "where emp_id = :empId and loan_id <> :excludeLoanId and balance > 0 "
            + "and cast(status as text) in ('Approved', 'Active')",
            nativeQuery = true)
    BigDecimal outstandingBalance(@Param("empId") String empId, @Param("excludeLoanId") Long excludeLoanId);

    default Page<LoanAdvance> findByStatusOrderByLoanIdDesc(LoanStatus status, Pageable pageable) {
        return findByStatusLabel(status.getLabel(), pageable);
    }

    default Page<LoanAdvance> findByEmpIdAndStatusOrderByLoanIdDesc(String empId, LoanStatus status, Pageable pageable) {
        return findByEmpIdAndStatusLabel(empId, status.getLabel(), pageable);
    }
}