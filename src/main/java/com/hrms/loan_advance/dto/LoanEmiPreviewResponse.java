package com.hrms.loan_advance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/** Backs the "Live EMI Preview" strip. */
@Getter
@AllArgsConstructor
public class LoanEmiPreviewResponse {
    private BigDecimal emi;
    private BigDecimal interestRate;
    private Short tenure;
}