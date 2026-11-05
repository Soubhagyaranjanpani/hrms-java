package com.hrms.loan_advance.domain;

/**
 * Java side of the Postgres enum `loan_type_enum`.
 * The API uses the constant name (e.g. PERSONAL_LOAN); the DB uses {@link #getLabel()}.
 *
 * Labels match: SELECT unnest(enum_range(NULL::loan_type_enum));
 * If that query returns more rows, add a constant for each one.
 */
public enum LoanType {
    SALARY_ADVANCE("Salary Advance"),
    PERSONAL_LOAN("Personal Loan"),
    EMERGENCY_LOAN("Emergency Loan");

    private final String label;

    LoanType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static LoanType fromLabel(String label) {
        for (LoanType t : values()) {
            if (t.label.equalsIgnoreCase(label) || t.name().equalsIgnoreCase(label)) return t;
        }
        throw new IllegalArgumentException("Unknown loan_type_enum value in DB: " + label);
    }
}

