package com.hrms.loan_advance.dto;

import com.hrms.loan_advance.domain.LoanType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** Body of calculateEmiPreview. Every field is optional so the preview also works on an empty form. */
@Getter
@Setter
public class LoanEmiPreviewRequest {
    private LoanType loanType;
    private BigDecimal amount;
    private Short tenure;
}