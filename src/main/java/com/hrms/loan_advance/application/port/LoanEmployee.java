package com.hrms.loan_advance.application.port;

/**
 * The only employee data the loan module needs. Plain values, no JPA entity, so the loan module
 * never depends on how the employee module stores things (or on whether it is even in the same app).
 */
public record LoanEmployee(
        Long id,
        String employeeCode,
        String fullName,
        String department,
        String grade,
        String managerCode      // employee code of the reporting manager, null if none
) {
}