package com.hrms.loan_advance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** One line of the "Eligibility Check" box: a green tick (passed) or a red cross. */
@Getter
@Setter
@AllArgsConstructor
public class LoanEligibilityCheck {
    private String label;
    private boolean passed;
}