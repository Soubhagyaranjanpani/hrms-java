package com.hrms.payroll.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.payroll.application.GetAllSalaryStructuresUseCase;
import com.hrms.payroll.application.*;
import com.hrms.payroll.dto.*;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final GetPayrollListUseCase             getListUseCase;
    private final GetPayrollDashboardStatsUseCase   getStatsUseCase;
    private final BulkGeneratePayrollUseCase        bulkGenerateUseCase;
    private final UpdatePayrollRecordUseCase        updateUseCase;
    private final ApprovePayrollUseCase             approveUseCase;
    private final ProcessPayrollUseCase             processUseCase;
    private final GetEmployeePayHistoryUseCase      payHistoryUseCase;
    private final SaveSalaryStructureUseCase        saveStructureUseCase;
    private final PayrollRepository                 payrollRepo;
    private final PayrollMapper                     mapper;
    private final GetAllSalaryStructuresUseCase getAllStructuresUseCase;
    private final SubmitPayrollForApprovalUseCase submitUseCase;
    private final PdfPayslipGenerator pdfGenerator;

    // ── GET /api/payroll?month=YYYY-MM ────────────────────────
    // Payroll run table rows
    @GetMapping
    public ApiResponse<List<PayrollRecordResponse>> getList(
            @RequestParam(defaultValue = "") String month) {
        return ResponseUtils.createSuccessResponse(getListUseCase.execute(month), new TypeReference<>() {});
    }

    // ── GET /api/payroll/stats?month=YYYY-MM ──────────────────
    // Dashboard stat cards + charts
    @GetMapping("/stats")
    public ApiResponse<PayrollDashboardStats> getStats(
            @RequestParam(defaultValue = "") String month) {
        return ResponseUtils.createSuccessResponse(getStatsUseCase.execute(month), new TypeReference<>() {});
    }

    // ── GET /api/payroll/months ───────────────────────────────
    // Month dropdown — returns ["2025-04","2025-03"...]
    @GetMapping("/months")
    public ApiResponse<List<String>> getMonths() {
        return ResponseUtils.createSuccessResponse(payrollRepo.findDistinctMonths(), new TypeReference<>() {});
    }

    // ── GET /api/payroll/{id} ─────────────────────────────────
    // Single record detail (payslip)
    @GetMapping("/{id}")
    public ApiResponse<PayrollRecordResponse> getOne(@PathVariable Long id) {
        PayrollRecordResponse r = payrollRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return ResponseUtils.createSuccessResponse(r, new TypeReference<>() {});
    }

    // ── GET /api/payroll/employee/{empId} ─────────────────────
    // Pay history for one employee (My Payslips tab)
    @GetMapping("/employee/{empId}")
    public ApiResponse<List<PayrollRecordResponse>> getEmployeeHistory(
            @PathVariable Long empId) {
        return ResponseUtils.createSuccessResponse(payHistoryUseCase.execute(empId), new TypeReference<>() {});
    }

    // ── POST /api/payroll/generate ────────────────────────────
    // Bulk-create DRAFT records for all active employees
    @PostMapping("/generate")
    public ApiResponse<String> generate(@RequestBody BulkGenerateRequest req) {
        return ResponseUtils.createSuccessResponse(bulkGenerateUseCase.execute(req), new TypeReference<>() {});
    }

    // ── PUT /api/payroll/{id} ─────────────────────────────────
    // Edit a DRAFT or PENDING record (earnings / deductions / LOP)
    @PutMapping("/{id}")
    public ApiResponse<PayrollRecordResponse> update(
            @PathVariable Long id,
            @RequestBody UpdatePayrollRequest req) {
        return ResponseUtils.createSuccessResponse(updateUseCase.execute(id, req), new TypeReference<>() {});
    }

    // ── POST /api/payroll/approve ─────────────────────────────
    // PENDING → APPROVED  (manager action)
    @PostMapping("/approve")
    public ApiResponse<String> approve(
            @RequestBody ApproveRequest req,
            Principal principal) {
        return ResponseUtils.createSuccessResponse(
                approveUseCase.execute(req.getYearMonth(), req.getRecordIds(), principal.getName()),
                new TypeReference<>() {});
    }

    // ── POST /api/payroll/process ─────────────────────────────
    // APPROVED → PROCESSED  (finance action, stamps payment date)
    @PostMapping("/process")
    public ApiResponse<String> process(
            @RequestBody ProcessPayrollRequest req,
            Principal principal) {
        return ResponseUtils.createSuccessResponse(
                processUseCase.execute(req, principal.getName()),
                new TypeReference<>() {});
    }

    // ── POST /api/payroll/structure ───────────────────────────
    // Save / update per-employee salary structure
    @PostMapping("/structure")
    public ApiResponse<String> saveStructure(@RequestBody SalaryStructureRequest req) {
        return ResponseUtils.createSuccessResponse(
                saveStructureUseCase.execute(req),
                new TypeReference<>() {}
        );
    }


    @GetMapping("/structure/all")
    public ApiResponse<List<SalaryStructureResponse>> getAllStructures() {
        return ResponseUtils.createSuccessResponse(
                getAllStructuresUseCase.execute(),
                new TypeReference<>() {}
        );
    }
    @PostMapping("/submit")
    public ApiResponse<String> submitForApproval(@RequestBody SubmitRequest req) {
        return ResponseUtils.createSuccessResponse(
                submitUseCase.execute(req.getRecordIds()),
                new TypeReference<>() {}
        );
    }


    @GetMapping("/{id}/payslip/pdf")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable Long id) {
        try {
            PayrollRecordResponse record = payrollRepo.findById(id)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new RuntimeException("Record not found"));

            byte[] pdfBytes = pdfGenerator.generatePayslip(record);  // Call generatePayslip, not generatePdf

            String filename = "Payslip_" +
                    (record.getEmployee() != null ? record.getEmployee().replaceAll("\\s+", "_") : "Employee") +
                    "_" + record.getPayrollMonth().replaceAll("\\s+", "_") + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

}