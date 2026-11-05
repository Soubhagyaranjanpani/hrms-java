package com.hrms.loan_advance.api;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.loan_advance.application.LoanAdvanceService;
import com.hrms.loan_advance.domain.LoanApprovalTab;
import com.hrms.loan_advance.domain.LoanStatus;
import com.hrms.loan_advance.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Thin HTTP layer: every endpoint is one call to LoanAdvanceService.
 * The URL of each endpoint is its API name, e.g. GET /api/loan-advances/getMyLoans.
 */
@RestController
@RequestMapping("/api/loan-advances")
@RequiredArgsConstructor
public class LoanAdvanceController {

    private final LoanAdvanceService loanService;

    // ---------- Apply for Loan form ----------

    /** Employee Details block + loan eligibility. Omit employeeId for the logged-in user. */
    @GetMapping("/getEmployeeLoanProfile")
    public ResponseEntity<ApiResponse<LoanFormDetailsResponse>> getEmployeeLoanProfile(
            @RequestParam(required = false) Long employeeId) {
        return ok(loanService.getEmployeeLoanProfile(employeeId));
    }

    /** "Loan Type" dropdown. */
    @GetMapping("/getLoanTypes")
    public ResponseEntity<ApiResponse<List<LoanTypeOption>>> getLoanTypes() {
        return ok(loanService.getLoanTypes());
    }

    /** "Live EMI Preview". */
    @PostMapping("/calculateEmiPreview")
    public ResponseEntity<ApiResponse<LoanEmiPreviewResponse>> calculateEmiPreview(
            @RequestBody LoanEmiPreviewRequest request) {
        return ok(loanService.calculateEmiPreview(request));
    }

    /** "Attach Document". Form field name: file. Send the returned `attachment` in submitLoanApplication. */
    @PostMapping(value = "/uploadAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LoanAttachmentResponse>> uploadAttachment(
            @RequestParam("file") MultipartFile file) throws IOException {
        try (InputStream content = file.getInputStream()) {
            return ok(loanService.uploadAttachment(file.getOriginalFilename(), file.getSize(), content));
        }
    }

    /** "Submit Application". */
    @PostMapping("/submitLoanApplication")
    public ResponseEntity<ApiResponse<LoanAdvanceResponse>> submitLoanApplication(
            @Valid @RequestBody LoanAdvanceCreateReq request) {
        return ok(loanService.submitLoanApplication(request));
    }

    // ---------- Employee ----------

    /** Loans of the logged-in employee, newest first. page is 0-based; size is capped at 100. */
    @GetMapping("/getMyLoans")
    public ResponseEntity<ApiResponse<LoanPageResponse<LoanAdvanceResponse>>> getMyLoans(
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ok(loanService.getMyLoans(status, page, size));
    }

    /** Cancel an application that is still pending approval. */
    @PutMapping("/cancelLoan/{loanId}")
    public ResponseEntity<ApiResponse<LoanAdvanceResponse>> cancelLoan(@PathVariable Long loanId) {
        return ok(loanService.cancelLoan(loanId));
    }

    // ---------- Approver ----------

    /** Approvals page: tabs (ALL, PENDING_FOR_ME, PENDING_APPROVAL, APPROVED, REJECTED), search and paging. */
    @GetMapping("/getLoanApprovals")
    public ResponseEntity<ApiResponse<LoanApprovalPageResponse>> getLoanApprovals(
            @RequestParam(defaultValue = "ALL") LoanApprovalTab tab,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ok(loanService.getLoanApprovals(tab, search, page, size));
    }

    /** One loan with salary, eligibility checks and the approval chain. */
    @GetMapping("/getLoanDetails/{loanId}")
    public ResponseEntity<ApiResponse<LoanDetailsResponse>> getLoanDetails(@PathVariable Long loanId) {
        return ok(loanService.getLoanDetails(loanId));
    }

    @GetMapping("/getApprovalChain/{loanId}")
    public ResponseEntity<ApiResponse<List<ApprovalStepResponse>>> getApprovalChain(@PathVariable Long loanId) {
        return ok(loanService.getApprovalChain(loanId));
    }

    /** Approve the current stage ("Approve & Forward"). Remarks optional. */
    @PutMapping("/approveLoan/{loanId}")
    public ResponseEntity<ApiResponse<LoanAdvanceResponse>> approveLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody(required = false) LoanActionRequest request) {
        return ok(loanService.approveLoan(loanId, request));
    }

    /** Reject the current stage. Remarks (the reason) are required. */
    @PutMapping("/rejectLoan/{loanId}")
    public ResponseEntity<ApiResponse<LoanAdvanceResponse>> rejectLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody(required = false) LoanActionRequest request) {
        return ok(loanService.rejectLoan(loanId, request));
    }

    /** The "View" button next to the attachment: streams the file. */
    @GetMapping("/downloadAttachment/{loanId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long loanId) {
        AttachmentFile file = loanService.downloadAttachment(loanId);
        MediaType mediaType = MediaTypeFactory.getMediaType(file.fileName()).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(file.fileName()).build().toString())
                .body(file.resource());
    }

    // ---------- Repayment ----------

    @GetMapping("/getRepaymentSchedule/{loanId}")
    public ResponseEntity<ApiResponse<List<LoanRepaymentResponse>>> getRepaymentSchedule(@PathVariable Long loanId) {
        return ok(loanService.getRepaymentSchedule(loanId));
    }

    /** Principal, outstanding balance and amount paid. */
    @GetMapping("/getLoanSummary/{loanId}")
    public ResponseEntity<ApiResponse<LoanSummaryResponse>> getLoanSummary(@PathVariable Long loanId) {
        return ok(loanService.getLoanSummary(loanId));
    }

    /** Record that an EMI was deducted in payroll (HR, Finance or Admin). */
    @PutMapping("/markEmiPaid/{loanId}")
    public ResponseEntity<ApiResponse<LoanSummaryResponse>> markEmiPaid(
            @PathVariable Long loanId, @Valid @RequestBody MarkEmiPaidRequest request) {
        return ok(loanService.markEmiPaid(loanId, request));
    }

    /** Early closure (HR, Finance or Admin). */
    @PostMapping("/forecloseLoan/{loanId}")
    public ResponseEntity<ApiResponse<LoanSummaryResponse>> forecloseLoan(@PathVariable Long loanId) {
        return ok(loanService.forecloseLoan(loanId));
    }

    private static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(ResponseUtils.createSuccessResponse(data, null));
    }
}