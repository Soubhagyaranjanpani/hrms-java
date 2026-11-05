package com.hrms.loan_advance.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** Read-only "Employee Details" block + eligibility hint of the Apply for Loan form. */
@Getter
@Setter
public class LoanFormDetailsResponse {
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String grade;
    private BigDecimal netSalary;          // latest monthly net salary, null if no payroll yet
    private BigDecimal maxEligibleAmount;  // 50% of netSalary, null if netSalary unknown
    private boolean eligible;              // false when no payroll exists: the form should disable Submit
    private String eligibilityMessage;     // reason to show when eligible == false, otherwise null
    private String nextLoanRefNo;          // preview only; the real one is fixed on submit
}