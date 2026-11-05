package com.hrms.loan_advance.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** The approver view of one loan: loan fields + salary, eligibility checks and the approval chain. */
@Getter
@Setter
public class LoanDetailsResponse extends LoanAdvanceResponse {
    private BigDecimal netSalary;                       // latest monthly net salary, null if no payroll
    private LocalDate appliedOn;
    private List<LoanEligibilityCheck> eligibilityChecks;
    private List<ApprovalStepResponse> approvalChain;
    private boolean canAct;                             // true: show the Approve and Reject buttons
    private String actionMessage;                       // e.g. "Waiting for HR Head. You can act only when ..."
}