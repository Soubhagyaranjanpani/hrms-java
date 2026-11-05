package com.hrms.loan_advance.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** One stage of the approval chain. Stored as JSON inside loan.approver_chain. */
@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApprovalStep {

    private int stage;                 // 1, 2, 3
    private ApprovalRole role;
    private String title;              // "Reporting Manager"
    private ApprovalStepStatus status;
    private String actedByCode;        // employee code of the person who approved/rejected
    private String actedByName;
    private String actedOn;            // ISO date-time text, e.g. 2026-11-02T15:50:12
    private String remarks;

    public static ApprovalStep of(int stage, ApprovalRole role) {
        ApprovalStep step = new ApprovalStep();
        step.stage = stage;
        step.role = role;
        step.title = role.getTitle();
        step.status = ApprovalStepStatus.WAITING;
        return step;
    }

    public void act(ApprovalStepStatus newStatus, String byCode, String byName, String remarksText, String at) {
        this.status = newStatus;
        this.actedByCode = byCode;
        this.actedByName = byName;
        this.remarks = remarksText;
        this.actedOn = at;
    }
}