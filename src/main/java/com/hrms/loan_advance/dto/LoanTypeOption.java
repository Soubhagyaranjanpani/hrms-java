package com.hrms.loan_advance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/** One entry of the "Loan Type" dropdown. */
@Getter
@AllArgsConstructor
public class LoanTypeOption {
    private String value;            // send this back as loanType
    private String label;
    private BigDecimal interestRate; // annual %, 0 = interest-free advance
}