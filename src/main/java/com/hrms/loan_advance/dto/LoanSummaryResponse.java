package com.hrms.loan_advance.dto;

import com.hrms.loan_advance.domain.LoanStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Principal, outstanding balance and amount paid of one loan. */
@Getter
@Setter
public class LoanSummaryResponse {
    private Long loanId;
    private String loanRefNo;
    private LoanStatus status;
    private String statusLabel;

    private BigDecimal principal;          // the amount that was lent
    private BigDecimal interestRate;       // annual %, flat
    private Short tenure;
    private BigDecimal emi;
    private BigDecimal totalPayable;       // principal + interest
    private BigDecimal amountPaid;         // sum of EMIs marked Paid
    private BigDecimal outstandingBalance; // still to be paid
    private int paidEmis;
    private int remainingEmis;
    private String nextDueMonth;           // null when nothing is left to pay
    private BigDecimal nextDueAmount;
    private LocalDate disbursedOn;
    private BigDecimal settledAmount;      // only set by forecloseLoan: the lump sum that closed the loan
}