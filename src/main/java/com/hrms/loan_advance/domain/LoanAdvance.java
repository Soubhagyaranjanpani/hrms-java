package com.hrms.loan_advance.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maps the existing `loan` table.
 *
 * emp_id holds the employee's employee_code (varchar(20)), not employees.id.
 * loan_type / status are Postgres enums: the converters translate to the DB labels, and
 * @ColumnTransformer casts the value to the enum type on insert/update (Postgres rejects a
 * plain varchar for an enum column). Reads return the label text as-is.
 * Filtering by status is done with native queries (cast(status as text) = ?).
 * approver_chain (jsonb) holds the sequential approval workflow as JSON (see ApprovalChain).
 * balance = what is still to be paid (unpaid EMIs); it is set when the loan is fully approved.
 */
@Entity
@Getter
@Setter
@Table(name = "loan")
public class LoanAdvance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "emp_id", length = 20, nullable = false)
    private String empId;

    @Convert(converter = LoanTypeConverter.class)
    @ColumnTransformer(write = "?::loan_type_enum")
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "tenure", nullable = false)
    private Short tenure;

    @Column(name = "emi", precision = 12, scale = 2, nullable = false)
    private BigDecimal emi;

    @Column(name = "purpose", length = 500, nullable = false)
    private String purpose;

    @Column(name = "attachment")
    private String attachment;

    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    @Convert(converter = LoanStatusConverter.class)
    @ColumnTransformer(write = "?::loan_status_enum")
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @Column(name = "disbursed_on")
    private LocalDate disbursedOn;

    @Column(name = "balance", precision = 12, scale = 2, nullable = false)
    private BigDecimal balance;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "approver_chain", columnDefinition = "jsonb")
    private ApprovalChain approverChain;

    /** NEW column (the form's "Preferred Recovery"). See loan_production_migration.sql. */
    @Column(name = "preferred_recovery", length = 30)
    private String preferredRecovery;

    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", length = 20)
    private String modifiedBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        if (requestedDate == null) requestedDate = LocalDate.now();
        if (status == null) status = LoanStatus.DRAFT;
        if (balance == null) balance = BigDecimal.ZERO;
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }
}