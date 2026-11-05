package com.hrms.loan_advance.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Body of markEmiPaid. */
@Getter
@Setter
public class MarkEmiPaidRequest {

    @NotNull(message = "EMI number is required")
    @Min(value = 1, message = "EMI number must be 1 or more")
    private Integer emiNo;

    /** Payslip the deduction appeared on, e.g. PS-2026-12-EMP444766. */
    @Size(max = 50, message = "Payslip reference is too long")
    private String payslipRef;
}