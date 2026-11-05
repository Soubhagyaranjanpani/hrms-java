package com.hrms.loan_advance.application;

import com.hrms.loan_advance.application.port.LoanEmployee;

/**
 * The logged-in user as the loan module sees them, built once per request by LoanAccessGuard.
 *
 * admin / hr / finance come from the user's role. HR and Finance role names are configurable
 * (hrms.loan.approval.hr-role, hrms.loan.approval.finance-role).
 */
public record LoanActor(LoanEmployee employee, boolean admin, boolean hr, boolean finance) {

    public String employeeCode() {
        return employee.employeeCode();
    }

    public String fullName() {
        return employee.fullName();
    }

    public boolean hrOrAdmin() {
        return admin || hr;
    }

    /** Admin, HR or Finance: may see every loan and record repayments. */
    public boolean privileged() {
        return admin || hr || finance;
    }
}