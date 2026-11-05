package com.hrms.loan_advance.dto;

import com.hrms.loan_advance.domain.ApprovalRole;
import com.hrms.loan_advance.domain.ApprovalStepStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** One row of the "Approval Chain" box. */
@Getter
@Setter
public class ApprovalStepResponse {
    private int stage;
    private ApprovalRole role;
    private String title;                 // "Reporting Manager"
    private ApprovalStepStatus status;
    private String statusLabel;           // "Pending", "Waiting", "Approved", "Rejected"
    private String actedByCode;
    private String actedByName;
    private LocalDateTime actedOn;
    private String remarks;
}