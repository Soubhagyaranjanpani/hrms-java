package com.hrms.loan_advance.dto;

import com.hrms.loan_advance.domain.LoanStatus;
import com.hrms.loan_advance.domain.LoanType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanAdvanceResponse {
    private Long loanId;
    private String loanRefNo;            // LN-0105

    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String grade;

    private LoanType loanType;
    private String loanTypeLabel;
    private BigDecimal amount;
    private Short tenure;
    private BigDecimal emi;
    private BigDecimal interestRate;     // annual %, informational

    private LocalDate requestedDisburseDate;
    private String preferredRecovery;
    private String purpose;
    private String attachment;           // stored key (send it back to downloadAttachment by loanId)
    private String attachmentName;       // original file name, e.g. quotation.pdf

    private LoanStatus status;
    private String statusLabel;
    private String waitingOn;            // title of the stage that must act, e.g. "HR Head"; null when not pending
    private boolean waitingOnMe;         // true: the logged-in user is the one to act ("You")
    private String rejectionReason;      // remarks of the stage that rejected it, otherwise null
    private LocalDate disbursedOn;
    private BigDecimal balance;          // still to be paid

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}