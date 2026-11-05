package com.hrms.loan_advance.infrastructure;

import com.hrms.loan_advance.domain.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Only LoanAdvanceService uses this repository. A loan has at most 12 EMIs, so no paging is needed. */
@Repository
public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {

    List<LoanRepayment> findByLoanIdOrderByEmiNoAsc(Long loanId);
}