package com.hrms.loan_advance.application;

import com.hrms.loan_advance.application.port.LoanEmployee;
import com.hrms.loan_advance.domain.*;
import com.hrms.loan_advance.dto.ApprovalStepResponse;
import com.hrms.loan_advance.dto.LoanAdvanceResponse;
import com.hrms.loan_advance.dto.LoanRepaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Pure mapping: no database access. The service hands it the loan and the employee already loaded. */
@Component
@RequiredArgsConstructor
public class LoanAdvanceMapper {

    /** Stored attachment keys look like "<36 char uuid>_<original name>". */
    private static final int ATTACHMENT_PREFIX_LENGTH = 37;

    private final LoanEmiCalculator calculator;

    public static String refNo(Long loanId) {
        return loanId == null ? null : String.format("LN-%04d", loanId);
    }

    /** "3f2c...-uuid_quotation.pdf" -> "quotation.pdf". */
    public static String attachmentName(String storedKey) {
        if (storedKey == null) return null;
        return storedKey.length() > ATTACHMENT_PREFIX_LENGTH && storedKey.charAt(ATTACHMENT_PREFIX_LENGTH - 1) == '_'
                ? storedKey.substring(ATTACHMENT_PREFIX_LENGTH)
                : storedKey;
    }

    /** emp may be null if the employee record no longer exists; the loan fields are still returned. */
    public LoanAdvanceResponse toResponse(LoanAdvance l, LoanEmployee emp) {
        LoanAdvanceResponse r = new LoanAdvanceResponse();
        fill(r, l, emp);
        return r;
    }

    /** Copies the loan into any response type, so LoanDetailsResponse can reuse it. */
    public void fill(LoanAdvanceResponse r, LoanAdvance l, LoanEmployee emp) {
        r.setLoanId(l.getLoanId());
        r.setLoanRefNo(refNo(l.getLoanId()));

        r.setEmployeeCode(l.getEmpId());
        if (emp != null) {
            r.setEmployeeId(emp.id());
            r.setEmployeeName(emp.fullName());
            r.setDepartment(emp.department());
            r.setGrade(emp.grade());
        }

        r.setLoanType(l.getLoanType());
        r.setLoanTypeLabel(l.getLoanType() != null ? l.getLoanType().getLabel() : null);
        r.setAmount(l.getAmount());
        r.setTenure(l.getTenure());
        r.setEmi(l.getEmi());
        r.setInterestRate(calculator.interestRateFor(l.getLoanType()));

        r.setRequestedDisburseDate(l.getRequestedDate());
        r.setPreferredRecovery(l.getPreferredRecovery());
        r.setPurpose(l.getPurpose());
        r.setAttachment(l.getAttachment());
        r.setAttachmentName(attachmentName(l.getAttachment()));

        r.setStatus(l.getStatus());
        r.setStatusLabel(l.getStatus() != null ? l.getStatus().getLabel() : null);
        r.setDisbursedOn(l.getDisbursedOn());
        r.setBalance(l.getBalance());
        r.setCreatedDate(l.getCreatedDate());
        r.setModifiedDate(l.getModifiedDate());

        ApprovalChain chain = l.getApproverChain();
        if (chain != null) {
            if (l.getStatus() == LoanStatus.PENDING_APPROVAL) {
                r.setWaitingOn(chain.activeStep().map(ApprovalStep::getTitle).orElse(null));
            }
            if (l.getStatus() == LoanStatus.REJECTED) {
                r.setRejectionReason(chain.rejectionRemarks().orElse(null));
            }
        }
    }

    public List<ApprovalStepResponse> toStepResponses(ApprovalChain chain) {
        if (chain == null || chain.getSteps() == null) return List.of();
        return chain.getSteps().stream().map(this::toStepResponse).toList();
    }

    private ApprovalStepResponse toStepResponse(ApprovalStep step) {
        ApprovalStepResponse r = new ApprovalStepResponse();
        r.setStage(step.getStage());
        r.setRole(step.getRole());
        r.setTitle(step.getTitle());
        r.setStatus(step.getStatus());
        r.setStatusLabel(step.getStatus() != null ? step.getStatus().getLabel() : null);
        r.setActedByCode(step.getActedByCode());
        r.setActedByName(step.getActedByName());
        r.setActedOn(parseDateTime(step.getActedOn()));
        r.setRemarks(step.getRemarks());
        return r;
    }

    public LoanRepaymentResponse toRepaymentResponse(LoanRepayment row) {
        LoanRepaymentResponse r = new LoanRepaymentResponse();
        r.setRepaymentId(row.getRepaymentId());
        r.setLoanId(row.getLoanId());
        r.setEmiNo(row.getEmiNo());
        r.setDueMonth(row.getDueMonth());
        r.setAmount(row.getAmount());
        r.setStatus(row.getStatus());
        r.setStatusLabel(row.getStatus() != null ? row.getStatus().getLabel() : null);
        r.setPayslipRef(row.getPayslipRef());
        return r;
    }

    private static LocalDateTime parseDateTime(String text) {
        if (text == null) return null;
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}