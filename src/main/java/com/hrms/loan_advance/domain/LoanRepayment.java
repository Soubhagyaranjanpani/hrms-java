package com.hrms.loan_advance.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Maps the existing `loan_repayment` table: one row per EMI of a loan.
 *
 * due_month is text such as '2026-12'. status holds Upcoming / Paid / Skipped / Foreclosed;
 * it is written with a cast to loan_status_enum (which also works when the column is plain text).
 */
@Entity
@Getter
@Setter
@Table(name = "loan_repayment")
public class LoanRepayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repayment_id")
    private Long repaymentId;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Column(name = "emi_no", nullable = false)
    private Integer emiNo;

    @Column(name = "due_month", nullable = false)
    private String dueMonth;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Convert(converter = LoanStatusConverter.class)
    @ColumnTransformer(write = "?::loan_status_enum")
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @Column(name = "payslip_ref")
    private String payslipRef;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = LoanStatus.UPCOMING;
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }
}