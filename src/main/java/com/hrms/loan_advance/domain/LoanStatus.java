package com.hrms.loan_advance.domain;

/**
 * Java side of the Postgres enum `loan_status_enum` (table default is 'Draft').
 * The API uses the constant name (e.g. PENDING_APPROVAL); the DB uses {@link #getLabel()}.
 *
 * Labels match: SELECT unnest(enum_range(NULL::loan_status_enum));
 */
public enum LoanStatus {
    // loan lifecycle
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    ACTIVE("Active"),
    CLOSED("Closed"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled"),
    FORECLOSED("Foreclosed"),
    // shared enum also holds the repayment (EMI) statuses
    UPCOMING("Upcoming"),
    PAID("Paid"),
    SKIPPED("Skipped");

    private final String label;

    LoanStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static LoanStatus fromLabel(String label) {
        for (LoanStatus s : values()) {
            if (s.label.equalsIgnoreCase(label) || s.name().equalsIgnoreCase(label)) return s;
        }
        throw new IllegalArgumentException("Unknown loan_status_enum value in DB: " + label);
    }
}
