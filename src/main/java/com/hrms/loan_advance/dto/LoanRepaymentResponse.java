package com.hrms.loan_advance.dto;

import com.hrms.loan_advance.domain.LoanStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** One EMI of the repayment schedule. */
@Getter
@Setter
public class LoanRepaymentResponse {
    private Long repaymentId;
    private Long loanId;
    private Integer emiNo;
    private String dueMonth;          // "2026-12"
    private BigDecimal amount;
    private LoanStatus status;        // UPCOMING, PAID, SKIPPED, FORECLOSED
    private String statusLabel;
    private String payslipRef;
}