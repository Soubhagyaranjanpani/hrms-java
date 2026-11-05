package com.hrms.loan_advance.domain;

/** The three approval stages of a loan request, in the order they are approved. */
public enum ApprovalRole {
    REPORTING_MANAGER("Reporting Manager"),
    HR_HEAD("HR Head"),
    FINANCE("Finance");

    private final String title;

    ApprovalRole(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}