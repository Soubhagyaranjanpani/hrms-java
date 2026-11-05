package com.hrms.loan_advance.dto;

import com.hrms.loan_advance.domain.LoanType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanAdvanceCreateReq {

    /** Employee the loan is for. Optional: defaults to the logged-in user (self-service). */
    @Positive(message = "Employee id must be a positive number (leave it out to apply for yourself)")
    private Long employeeId;

    @NotNull(message = "Loan Type is required")
    private LoanType loanType;

    @NotNull(message = "Loan Amount is required")
    @DecimalMin(value = "1", message = "Loan Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Loan Amount is invalid")
    private BigDecimal amount;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be between 1 and 12 months")
    @Max(value = 12, message = "Tenure must be between 1 and 12 months")
    private Short tenure;

    @FutureOrPresent(message = "Requested Disburse Date cannot be in the past")
    private LocalDate requestedDisburseDate;

    /** e.g. PAYROLL_DEDUCTION. Defaults to PAYROLL_DEDUCTION. */
    @Size(max = 30, message = "Preferred Recovery is too long")
    private String preferredRecovery;

    /** The `attachment` key returned by uploadAttachment (upload the file first). Leave out for no document. */
    @Size(max = 255, message = "Attachment name is too long")
    private String attachment;

    @NotBlank(message = "Purpose is required")
    @Size(max = 500, message = "Purpose cannot exceed 500 characters")
    private String purpose;
}