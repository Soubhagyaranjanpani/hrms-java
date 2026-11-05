package com.hrms.loan_advance.application;

import com.hrms.loan_advance.application.port.LoanAttachmentStorage;
import com.hrms.loan_advance.application.port.LoanEmployee;
import com.hrms.loan_advance.application.port.LoanEmployeePort;
import com.hrms.loan_advance.domain.*;
import com.hrms.loan_advance.dto.*;
import com.hrms.loan_advance.infrastructure.LoanAdvanceRepository;
import com.hrms.loan_advance.infrastructure.LoanApprovalQueryDao;
import com.hrms.loan_advance.infrastructure.LoanApprovalQueryDao.ApprovalScope;
import com.hrms.loan_advance.infrastructure.LoanApprovalQueryDao.ApprovalSearch;
import com.hrms.loan_advance.infrastructure.LoanRepaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The single entry point of the loan module. The controller talks only to this class, and this is the
 * only class that uses the loan repositories. Everything outside the module (employees, payroll, the
 * logged-in user, file storage) is reached through ports, so the module can be split into its own
 * microservice by replacing those adapters.
 *
 * Reads are read-only transactions; write methods override that with their own @Transactional.
 * Method names are the API names (getMyLoans, approveLoan, ...).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanAdvanceService {

    private static final String DEFAULT_RECOVERY = "PAYROLL_DEDUCTION";
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_ATTACHMENT_TYPES = Set.of("pdf", "png", "jpg", "jpeg");
    private static final Pattern LOAN_REFERENCE = Pattern.compile("(?i)^\\s*(?:LN-?)?0*(\\d+)\\s*$");
    private static final String NO_PAYROLL_MESSAGE =
            "No payroll record found for this employee, so loan eligibility cannot be verified";


    private final LoanAdvanceRepository loanRepository;
    private final LoanRepaymentRepository repaymentRepository;
    private final LoanApprovalQueryDao approvalQueryDao;
    private final LoanEmployeePort employees;
    private final LoanAttachmentStorage attachmentStorage;
    private final LoanAccessGuard guard;
    private final LoanEmiCalculator calculator;
    private final LoanEligibilityChecker eligibilityChecker;
    private final LoanRepaymentScheduleFactory scheduleFactory;
    private final LoanAdvanceMapper mapper;

    /**
     * true (default): an employee with no payroll record cannot apply, because the 50% cap cannot be checked.
     * false: the cap is skipped for such employees (handy on a local DB with no payroll data).
     */
    @Value("${hrms.loan.require-payroll:true}")
    private boolean requirePayroll = true;

    /** Largest accepted attachment, in bytes (default 5 MB). */
    @Value("${hrms.loan.attachment-max-bytes:5242880}")
    private long attachmentMaxBytes = 5_242_880L;

    // =====================================================================================
    // Apply for Loan form
    // =====================================================================================

    /** getEmployeeLoanProfile: employee details block + loan eligibility. employeeId null = the logged-in user. */
    public LoanFormDetailsResponse getEmployeeLoanProfile(Long employeeId) {
        LoanActor me = guard.currentActor();
        LoanEmployee employee = employeeId == null ? me.employee() : findEmployee(employeeId);
        guard.assertCanAccess(me, employee.employeeCode());

        Optional<BigDecimal> netSalary = employees.latestNetSalary(employee.id());
        boolean eligible = netSalary.isPresent() || !requirePayroll;

        LoanFormDetailsResponse r = new LoanFormDetailsResponse();
        r.setEmployeeId(employee.id());
        r.setEmployeeCode(employee.employeeCode());
        r.setEmployeeName(employee.fullName());
        r.setDepartment(employee.department());
        r.setGrade(employee.grade());
        r.setNetSalary(netSalary.orElse(null));
        r.setMaxEligibleAmount(calculator.maxEligibleAmount(netSalary.orElse(null)));
        r.setEligible(eligible);
        r.setEligibilityMessage(eligible ? null : NO_PAYROLL_MESSAGE);

        long nextId = loanRepository.findTopByOrderByLoanIdDesc().map(l -> l.getLoanId() + 1).orElse(1L);
        r.setNextLoanRefNo(LoanAdvanceMapper.refNo(nextId));
        return r;
    }

    /** getLoanTypes: the "Loan Type" dropdown. */
    public List<LoanTypeOption> getLoanTypes() {
        return Arrays.stream(LoanType.values())
                .map(t -> new LoanTypeOption(t.name(), t.getLabel(), calculator.interestRateFor(t)))
                .toList();
    }

    /** calculateEmiPreview: "Live EMI Preview". All inputs optional so it works on an empty form. */
    public LoanEmiPreviewResponse calculateEmiPreview(LoanEmiPreviewRequest request) {
        Short tenure = request.getTenure();
        short months = tenure == null || tenure < 0 ? 0 : tenure;
        return new LoanEmiPreviewResponse(
                calculator.calculateEmi(request.getAmount(), months, request.getLoanType()),
                calculator.interestRateFor(request.getLoanType()),
                months);
    }

    /** uploadAttachment: saves the document and returns the key to send in submitLoanApplication. */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public LoanAttachmentResponse uploadAttachment(String originalFileName, long sizeBytes, InputStream content) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw badRequest("File name is missing");
        }
        if (sizeBytes <= 0) {
            throw badRequest("The file is empty");
        }
        if (sizeBytes > attachmentMaxBytes) {
            throw badRequest("The file is too large (maximum " + (attachmentMaxBytes / (1024 * 1024)) + " MB)");
        }
        int dot = originalFileName.lastIndexOf('.');
        String extension = dot < 0 ? "" : originalFileName.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_ATTACHMENT_TYPES.contains(extension)) {
            throw badRequest("Only PDF, PNG and JPG files are allowed");
        }

        try {
            String key = attachmentStorage.store(originalFileName, content);
            return new LoanAttachmentResponse(key, LoanAdvanceMapper.attachmentName(key), sizeBytes);
        } catch (IOException e) {
            log.error("[LOAN] Could not store attachment {}", originalFileName, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store the attachment");
        }
    }

    /** downloadAttachment (the "View" button): the file of one loan. */
    public AttachmentFile downloadAttachment(Long loanId) {
        LoanActor me = guard.currentActor();
        LoanAdvance loan = findLoan(loanId);
        guard.assertCanView(me, loan);

        if (loan.getAttachment() == null) {
            throw notFound("This loan has no attachment");
        }
        Resource resource = attachmentStorage.load(loan.getAttachment())
                .orElseThrow(() -> notFound("Attachment file not found"));
        return new AttachmentFile(resource, LoanAdvanceMapper.attachmentName(loan.getAttachment()));
    }

    /** submitLoanApplication: "Submit Application". Starts the approval chain. */
    @Transactional
    public LoanAdvanceResponse submitLoanApplication(LoanAdvanceCreateReq request) {
        LoanActor me = guard.currentActor();
        LoanEmployee employee = request.getEmployeeId() == null ? me.employee() : findEmployee(request.getEmployeeId());

        guard.assertCanAccess(me, employee.employeeCode());
        validateAttachmentExists(request.getAttachment());
        validateEligibility(employee.id(), request.getAmount());

        LoanAdvance loan = new LoanAdvance();
        loan.setEmpId(employee.employeeCode());
        loan.setLoanType(request.getLoanType());
        loan.setAmount(request.getAmount());
        loan.setTenure(request.getTenure());
        loan.setEmi(calculator.calculateEmi(request.getAmount(), request.getTenure(), request.getLoanType()));
        loan.setPurpose(request.getPurpose().trim());
        loan.setAttachment(blankToNull(request.getAttachment()));
        loan.setRequestedDate(request.getRequestedDisburseDate());   // null -> today (@PrePersist)
        loan.setPreferredRecovery(blankToNull(request.getPreferredRecovery()) != null
                ? request.getPreferredRecovery().trim() : DEFAULT_RECOVERY);
        loan.setStatus(LoanStatus.PENDING_APPROVAL);
        loan.setBalance(BigDecimal.ZERO);
        loan.setApproverChain(ApprovalChain.start(employee.managerCode()));
        loan.setCreatedBy(me.employeeCode());

        return respond(loanRepository.save(loan), employee, me);
    }

    // =====================================================================================
    // Employee: my loans, cancel
    // =====================================================================================

    /** getMyLoans: loans of the logged-in employee, newest first. */
    public LoanPageResponse<LoanAdvanceResponse> getMyLoans(LoanStatus status, int page, int size) {
        LoanActor me = guard.currentActor();
        return toPage(findPage(me.employeeCode(), status, pageable(page, size)), me);
    }

    /** cancelLoan: withdraw an application that is still waiting for approval. */
    @Transactional
    public LoanAdvanceResponse cancelLoan(Long loanId) {
        LoanActor me = guard.currentActor();
        LoanAdvance loan = findLoan(loanId);
        guard.assertCanAccess(me, loan.getEmpId());

        if (loan.getStatus() != LoanStatus.PENDING_APPROVAL && loan.getStatus() != LoanStatus.DRAFT) {
            throw badRequest("Only a loan application that is pending approval can be cancelled");
        }
        loan.setStatus(LoanStatus.CANCELLED);
        if (loan.getApproverChain() != null) {
            loan.getApproverChain().close();
        }
        loan.setModifiedBy(me.employeeCode());

        LoanAdvance saved = loanRepository.save(loan);
        return respond(saved, employees.findByCode(saved.getEmpId()).orElse(null), me);
    }

    // =====================================================================================
    // Approver: details, chain, approve, reject, approvals list
    // =====================================================================================

    /** getLoanDetails: approver view of one loan. */
    public LoanDetailsResponse getLoanDetails(Long loanId) {
        LoanActor me = guard.currentActor();
        LoanAdvance loan = findLoan(loanId);
        guard.assertCanView(me, loan);

        LoanEmployee employee = employees.findByCode(loan.getEmpId()).orElse(null);
        Optional<BigDecimal> netSalary = employee == null
                ? Optional.empty() : employees.latestNetSalary(employee.id());
        BigDecimal outstanding = loanRepository.outstandingBalance(loan.getEmpId(), loan.getLoanId());

        LoanDetailsResponse r = new LoanDetailsResponse();
        mapper.fill(r, loan, employee);
        r.setNetSalary(netSalary.orElse(null));
        r.setAppliedOn(loan.getCreatedDate() != null ? loan.getCreatedDate().toLocalDate() : loan.getRequestedDate());
        r.setEligibilityChecks(eligibilityChecker.check(loan.getAmount(), loan.getTenure(), netSalary, outstanding));
        r.setApprovalChain(mapper.toStepResponses(loan.getApproverChain()));

        boolean canAct = guard.canActOnCurrentStep(me, loan);
        r.setCanAct(canAct);
        r.setWaitingOnMe(canAct);
        r.setActionMessage(actionMessage(loan, me, canAct, r.getWaitingOn()));
        return r;
    }

    /** getApprovalChain: the list of approvers and where the request is now. */
    public List<ApprovalStepResponse> getApprovalChain(Long loanId) {
        LoanActor me = guard.currentActor();
        LoanAdvance loan = findLoan(loanId);
        guard.assertCanView(me, loan);
        return mapper.toStepResponses(loan.getApproverChain());
    }

    /**
     * approveLoan: approves the stage that is pending. After the last stage the loan is Approved, the
     * money is considered disbursed today, and the EMI schedule is created.
     */
    @Transactional
    public LoanAdvanceResponse approveLoan(Long loanId, LoanActionRequest request) {
        LoanActor me = guard.currentActor();
        LoanAdvance loan = findLoan(loanId);
        requirePendingApproval(loan);
        guard.assertCanAct(me, loan);

        ApprovalChain chain = loan.getApproverChain();
        boolean wasLastStage = chain.approveActive(me.employeeCode(), me.fullName(), remarksOf(request), now());
        loan.setApproverChain(chain);

        if (wasLastStage) {
            loan.setStatus(LoanStatus.APPROVED);
            loan.setDisbursedOn(LocalDate.now());
            loan.setBalance(calculator.totalPayable(loan.getAmount(), loan.getTenure(), loan.getLoanType()));
        }
        loan.setModifiedBy(me.employeeCode());
        LoanAdvance saved = loanRepository.save(loan);

        if (wasLastStage && repaymentRepository.findByLoanIdOrderByEmiNoAsc(saved.getLoanId()).isEmpty()) {
            repaymentRepository.saveAll(scheduleFactory.build(saved));
        }
        return respond(saved, employees.findByCode(saved.getEmpId()).orElse(null), me);
    }

    /** rejectLoan: rejects the pending stage; the whole request is closed. A reason is required. */
    @Transactional
    public LoanAdvanceResponse rejectLoan(Long loanId, LoanActionRequest request) {
        String remarks = remarksOf(request);
        if (remarks == null) {
            throw badRequest("Remarks are required for rejection");
        }

        LoanActor me = guard.currentActor();
        LoanAdvance loan = findLoan(loanId);
        requirePendingApproval(loan);
        guard.assertCanAct(me, loan);

        ApprovalChain chain = loan.getApproverChain();
        chain.rejectActive(me.employeeCode(), me.fullName(), remarks, now());
        loan.setApproverChain(chain);
        loan.setStatus(LoanStatus.REJECTED);
        loan.setModifiedBy(me.employeeCode());

        LoanAdvance saved = loanRepository.save(loan);
        return respond(saved, employees.findByCode(saved.getEmpId()).orElse(null), me);
    }

    /**
     * getLoanApprovals: the Approvals page. Admin, HR and Finance see every loan; a reporting manager
     * sees the loans of their team. tabCounts are the numbers on the filter tabs.
     */
    public LoanApprovalPageResponse getLoanApprovals(LoanApprovalTab tab, String search, int page, int size) {
        LoanActor me = guard.currentActor();
        LoanApprovalTab activeTab = tab == null ? LoanApprovalTab.ALL : tab;
        ApprovalScope scope = new ApprovalScope(me.employeeCode(), me.privileged(), me.admin(), me.hr(),
                me.finance(), guard.allowsSelfApproval());

        Page<LoanAdvance> result = approvalQueryDao.find(scope, activeTab, buildSearch(search), pageable(page, size));

        Map<String, Long> tabCounts = new LinkedHashMap<>();
        for (LoanApprovalTab t : LoanApprovalTab.values()) {
            tabCounts.put(t.name(), approvalQueryDao.count(scope, t));
        }
        return LoanApprovalPageResponse.of(toPage(result, me), tabCounts);
    }

    // =====================================================================================
    // Repayment
    // =====================================================================================

    /** getRepaymentSchedule: every EMI of the loan. Empty until the loan is fully approved. */
    public List<LoanRepaymentResponse> getRepaymentSchedule(Long loanId) {
        LoanActor me = guard.currentActor();
        LoanAdvance loan = findLoan(loanId);
        guard.assertCanView(me, loan);
        return repaymentRepository.findByLoanIdOrderByEmiNoAsc(loanId).stream()
                .map(mapper::toRepaymentResponse).toList();
    }

    /** getLoanSummary: principal, outstanding balance, amount paid. */
    public LoanSummaryResponse getLoanSummary(Long loanId) {
        LoanActor me = guard.currentActor();
        LoanAdvance loan = findLoan(loanId);
        guard.assertCanView(me, loan);
        return buildSummary(loan, repaymentRepository.findByLoanIdOrderByEmiNoAsc(loanId), null);
    }

    /** markEmiPaid: records that the EMI was deducted in payroll. EMIs must be paid in order. */
    @Transactional
    public LoanSummaryResponse markEmiPaid(Long loanId, MarkEmiPaidRequest request) {
        LoanActor me = guard.currentActor();
        guard.assertPrivileged(me);
        LoanAdvance loan = findLoan(loanId);
        requireRunning(loan);

        List<LoanRepayment> rows = repaymentRepository.findByLoanIdOrderByEmiNoAsc(loanId);
        LoanRepayment target = rows.stream()
                .filter(row -> row.getEmiNo().equals(request.getEmiNo()))
                .findFirst()
                .orElseThrow(() -> notFound("EMI " + request.getEmiNo() + " was not found for this loan"));

        if (target.getStatus() != LoanStatus.UPCOMING) {
            throw badRequest("EMI " + target.getEmiNo() + " is already " + target.getStatus().getLabel());
        }
        int firstUnpaid = rows.stream().filter(row -> row.getStatus() == LoanStatus.UPCOMING)
                .mapToInt(LoanRepayment::getEmiNo).min().orElse(target.getEmiNo());
        if (target.getEmiNo() != firstUnpaid) {
            throw badRequest("EMI " + firstUnpaid + " must be marked paid first");
        }

        target.setStatus(LoanStatus.PAID);
        target.setPayslipRef(blankToNull(request.getPayslipRef()));
        loan.setBalance(loan.getBalance().subtract(target.getAmount()).max(BigDecimal.ZERO));

        boolean nothingLeft = rows.stream().noneMatch(row -> row.getStatus() == LoanStatus.UPCOMING);
        if (nothingLeft) {
            loan.setStatus(LoanStatus.CLOSED);
            loan.setBalance(BigDecimal.ZERO.setScale(2));
        }
        loan.setModifiedBy(me.employeeCode());

        repaymentRepository.save(target);
        loanRepository.save(loan);
        return buildSummary(loan, rows, null);
    }

    /**
     * forecloseLoan: early closure. The remaining balance is settled in one go: every unpaid EMI becomes
     * Foreclosed and the loan is closed. The settled amount is the balance still owed (flat-rate interest,
     * no rebate).
     */
    @Transactional
    public LoanSummaryResponse forecloseLoan(Long loanId) {
        LoanActor me = guard.currentActor();
        guard.assertPrivileged(me);
        LoanAdvance loan = findLoan(loanId);
        requireRunning(loan);

        List<LoanRepayment> rows = repaymentRepository.findByLoanIdOrderByEmiNoAsc(loanId);
        BigDecimal settled = loan.getBalance();

        List<LoanRepayment> unpaid = rows.stream().filter(row -> row.getStatus() == LoanStatus.UPCOMING).toList();
        unpaid.forEach(row -> row.setStatus(LoanStatus.FORECLOSED));
        repaymentRepository.saveAll(unpaid);

        loan.setStatus(LoanStatus.FORECLOSED);
        loan.setBalance(BigDecimal.ZERO.setScale(2));
        loan.setModifiedBy(me.employeeCode());
        loanRepository.save(loan);

        return buildSummary(loan, rows, settled);
    }

    // =====================================================================================
    // Private helpers
    // =====================================================================================

    /** Maps a saved loan to the response and marks whether the logged-in user is the one to act. */
    private LoanAdvanceResponse respond(LoanAdvance loan, LoanEmployee employee, LoanActor me) {
        LoanAdvanceResponse response = mapper.toResponse(loan, employee);
        response.setWaitingOnMe(guard.canActOnCurrentStep(me, loan));
        return response;
    }

    /** empCode null = every employee. */
    private Page<LoanAdvance> findPage(String empCode, LoanStatus status, Pageable pageable) {
        if (empCode == null) {
            return status == null
                    ? loanRepository.findAllByOrderByLoanIdDesc(pageable)
                    : loanRepository.findByStatusOrderByLoanIdDesc(status, pageable);
        }
        return status == null
                ? loanRepository.findByEmpIdOrderByLoanIdDesc(empCode, pageable)
                : loanRepository.findByEmpIdAndStatusOrderByLoanIdDesc(empCode, status, pageable);
    }

    /** One employee lookup for the whole page instead of one per row. */
    private LoanPageResponse<LoanAdvanceResponse> toPage(Page<LoanAdvance> page, LoanActor me) {
        Set<String> codes = page.getContent().stream().map(LoanAdvance::getEmpId).collect(Collectors.toSet());
        Map<String, LoanEmployee> byCode = employees.findByCodes(codes);
        return LoanPageResponse.of(page.map(loan -> respond(loan, byCode.get(loan.getEmpId()), me)));
    }

    /** Page index below 0 becomes 0; size is kept between 1 and MAX_PAGE_SIZE. Never sorted by the caller. */
    private static Pageable pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE));
    }

    /** Turns the search box text into: a loan number (LN-0091 or 91), employee codes by name/code, and a text match. */
    private ApprovalSearch buildSearch(String search) {
        String text = search == null ? "" : search.trim();
        if (text.isEmpty()) return ApprovalSearch.none();

        Long loanId = null;
        Matcher matcher = LOAN_REFERENCE.matcher(text);
        if (matcher.matches()) {
            try {
                loanId = Long.parseLong(matcher.group(1));
            } catch (NumberFormatException ignored) {
                // number too large to be a loan id: search by text only
            }
        }
        String escaped = text.toLowerCase(Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return new ApprovalSearch("%" + escaped + "%", loanId, employees.searchEmployeeCodes(text));
    }

    /** Max eligible = 50% of latest net salary. Without payroll the loan is rejected (unless switched off). */
    private void validateEligibility(Long employeeId, BigDecimal amount) {
        Optional<BigDecimal> netSalary = employees.latestNetSalary(employeeId);
        if (netSalary.isEmpty()) {
            if (requirePayroll) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_PAYROLL_MESSAGE);
            }
            return;
        }
        BigDecimal max = calculator.maxEligibleAmount(netSalary.get());
        if (amount.compareTo(max) > 0) {
            throw badRequest("Requested amount exceeds the maximum eligible amount of " + max);
        }
    }

    private void validateAttachmentExists(String attachment) {
        if (blankToNull(attachment) != null && !attachmentStorage.exists(attachment.trim())) {
            throw badRequest("Attachment not found. Upload the file with uploadAttachment first and send the returned key");
        }
    }

    private LoanSummaryResponse buildSummary(LoanAdvance loan, List<LoanRepayment> rows, BigDecimal settledAmount) {
        BigDecimal amountPaid = rows.stream()
                .filter(row -> row.getStatus() == LoanStatus.PAID)
                .map(LoanRepayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int paidEmis = (int) rows.stream().filter(row -> row.getStatus() == LoanStatus.PAID).count();
        List<LoanRepayment> upcoming = rows.stream().filter(row -> row.getStatus() == LoanStatus.UPCOMING).toList();
        Optional<LoanRepayment> next = upcoming.stream().min(Comparator.comparing(LoanRepayment::getEmiNo));

        LoanSummaryResponse r = new LoanSummaryResponse();
        r.setLoanId(loan.getLoanId());
        r.setLoanRefNo(LoanAdvanceMapper.refNo(loan.getLoanId()));
        r.setStatus(loan.getStatus());
        r.setStatusLabel(loan.getStatus().getLabel());
        r.setPrincipal(loan.getAmount());
        r.setInterestRate(calculator.interestRateFor(loan.getLoanType()));
        r.setTenure(loan.getTenure());
        r.setEmi(loan.getEmi());
        r.setTotalPayable(calculator.totalPayable(loan.getAmount(), loan.getTenure(), loan.getLoanType()));
        r.setAmountPaid(amountPaid.setScale(2));
        r.setOutstandingBalance(loan.getBalance());
        r.setPaidEmis(paidEmis);
        r.setRemainingEmis(upcoming.size());
        r.setNextDueMonth(next.map(LoanRepayment::getDueMonth).orElse(null));
        r.setNextDueAmount(next.map(LoanRepayment::getAmount).orElse(null));
        r.setDisbursedOn(loan.getDisbursedOn());
        r.setSettledAmount(settledAmount);
        return r;
    }

    /** The yellow note under the chain when the logged-in user cannot act. */
    private String actionMessage(LoanAdvance loan, LoanActor me, boolean canAct, String waitingOn) {
        if (loan.getStatus() != LoanStatus.PENDING_APPROVAL || canAct) return null;
        if (!guard.allowsSelfApproval() && guard.isOwner(me, loan.getEmpId())) {
            return "You cannot approve or reject your own loan request.";
        }
        return "Waiting for " + (waitingOn != null ? waitingOn : "the next approver")
                + ". You can act only when this request reaches your stage.";
    }

    private void requirePendingApproval(LoanAdvance loan) {
        if (loan.getStatus() != LoanStatus.PENDING_APPROVAL || loan.getApproverChain() == null) {
            throw badRequest("Only a loan that is pending approval can be approved or rejected");
        }
    }

    private void requireRunning(LoanAdvance loan) {
        if (loan.getStatus() != LoanStatus.APPROVED && loan.getStatus() != LoanStatus.ACTIVE) {
            throw badRequest("This loan is not running (status: " + loan.getStatus().getLabel() + ")");
        }
    }

    private LoanAdvance findLoan(Long loanId) {
        return loanRepository.findById(loanId).orElseThrow(() -> notFound("Loan request not found"));
    }

    private LoanEmployee findEmployee(Long employeeId) {
        return employees.findById(employeeId).orElseThrow(() -> notFound("Employee not found"));
    }

    private static String remarksOf(LoanActionRequest request) {
        return request == null ? null : blankToNull(request.getRemarks());
    }

    private static String now() {
        return LocalDateTime.now().withNano(0).toString();
    }

    private static ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}