package com.hrms.loan_advance.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The sequential approval workflow of one loan: Reporting Manager, then HR Head, then Finance.
 * Stored as JSON in loan.approver_chain. If the applicant has no reporting manager the chain
 * starts at HR Head.
 *
 * currentRole and currentApproverCode are copies of "who must act now". They exist so the
 * "Pending for me" list can be filtered in SQL without reading every chain.
 *
 * Plain Java methods here are named without get/is so Jackson does not write them into the JSON.
 */
@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApprovalChain {

    private String reportingManagerCode;
    private ApprovalRole currentRole;          // null once the chain is finished
    private String currentApproverCode;        // only set while the Reporting Manager stage is pending
    private List<ApprovalStep> steps = new ArrayList<>();

    public static ApprovalChain start(String reportingManagerCode) {
        ApprovalChain chain = new ApprovalChain();
        chain.reportingManagerCode = reportingManagerCode;

        int stage = 1;
        if (reportingManagerCode != null && !reportingManagerCode.isBlank()) {
            chain.steps.add(ApprovalStep.of(stage++, ApprovalRole.REPORTING_MANAGER));
        } else {
            chain.reportingManagerCode = null;
        }
        chain.steps.add(ApprovalStep.of(stage++, ApprovalRole.HR_HEAD));
        chain.steps.add(ApprovalStep.of(stage, ApprovalRole.FINANCE));

        chain.steps.get(0).setStatus(ApprovalStepStatus.PENDING);
        chain.refreshPointers();
        return chain;
    }

    /** The stage that must act now, if any. */
    public Optional<ApprovalStep> activeStep() {
        return steps.stream().filter(s -> s.getStatus() == ApprovalStepStatus.PENDING).findFirst();
    }

    /** Approves the active stage and opens the next one. Returns true when that was the last stage. */
    public boolean approveActive(String byCode, String byName, String remarks, String at) {
        ApprovalStep active = activeStep()
                .orElseThrow(() -> new IllegalStateException("No pending approval stage"));
        active.act(ApprovalStepStatus.APPROVED, byCode, byName, remarks, at);

        Optional<ApprovalStep> next = steps.stream()
                .filter(s -> s.getStatus() == ApprovalStepStatus.WAITING).findFirst();
        next.ifPresent(s -> s.setStatus(ApprovalStepStatus.PENDING));
        refreshPointers();
        return next.isEmpty();
    }

    /** Rejects the active stage. The later stages stay WAITING and the chain is finished. */
    public void rejectActive(String byCode, String byName, String remarks, String at) {
        ApprovalStep active = activeStep()
                .orElseThrow(() -> new IllegalStateException("No pending approval stage"));
        active.act(ApprovalStepStatus.REJECTED, byCode, byName, remarks, at);
        currentRole = null;
        currentApproverCode = null;
    }

    /** Used when the loan is cancelled: nobody has to act any more. */
    public void close() {
        currentRole = null;
        currentApproverCode = null;
    }

    /** The remarks of the stage that rejected the loan, if any. */
    public Optional<String> rejectionRemarks() {
        return steps.stream().filter(s -> s.getStatus() == ApprovalStepStatus.REJECTED)
                .map(ApprovalStep::getRemarks).findFirst();
    }

    private void refreshPointers() {
        Optional<ApprovalStep> active = activeStep();
        if (active.isPresent()) {
            currentRole = active.get().getRole();
            currentApproverCode = currentRole == ApprovalRole.REPORTING_MANAGER ? reportingManagerCode : null;
        } else {
            currentRole = null;
            currentApproverCode = null;
        }
    }
}