package com.hrms.loan_advance.domain;

/** WAITING = not its turn yet, PENDING = the stage that must act now. */
public enum ApprovalStepStatus {
    WAITING("Waiting"),
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String label;

    ApprovalStepStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}