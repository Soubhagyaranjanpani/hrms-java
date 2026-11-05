package com.hrms.loan_advance.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Body of approveLoan / rejectLoan. Remarks are optional for approval and required for rejection. */
@Getter
@Setter
public class LoanActionRequest {

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
}