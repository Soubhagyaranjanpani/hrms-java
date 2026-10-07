package com.hrms.payroll.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.payroll.application.GetAllSalaryStructuresUseCase;
import com.hrms.payroll.application.*;
import com.hrms.payroll.domain.SalaryConfiguration;
import com.hrms.payroll.dto.*;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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
    private final SalaryConfigService configService;

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

//    // ── POST /api/payroll/generate ────────────────────────────
//    // Bulk-create DRAFT records for all active employees
//    @PostMapping("/generate")
//    public ApiResponse<String> generate(@RequestBody BulkGenerateRequest req) {
//        return ResponseUtils.createSuccessResponse(bulkGenerateUseCase.execute(req), new TypeReference<>() {});
//    }

    // ── PUT /api/payroll/{id} ─────────────────────────────────
    // Edit a DRAFT or PENDING record (earnings / deductions / LOP)
    @PutMapping("/{id}")
    public ApiResponse<PayrollRecordResponse> update(
            @PathVariable Long id,
            @RequestBody UpdatePayrollRequest req) {
        return ResponseUtils.createSuccessResponse(updateUseCase.execute(id, req), new TypeReference<>() {});
    }

    // ── POST /api/payroll/approve ─────────────────────────────
//    // PENDING → APPROVED  (manager action)
//    @PostMapping("/approve")
//    public ApiResponse<String> approve(
//            @RequestBody ApproveRequest req,
//            Principal principal) {
//        return ResponseUtils.createSuccessResponse(
//                approveUseCase.execute(req.getYearMonth(), req.getRecordIds(), principal.getName()),
//                new TypeReference<>() {});
//    }

    // ── POST /api/payroll/process ─────────────────────────────
    // APPROVED → PROCESSED  (finance action, stamps payment date)
//    @PostMapping("/process")
//    public ApiResponse<String> process(
//            @RequestBody ProcessPayrollRequest req,
//            Principal principal) {
//        return ResponseUtils.createSuccessResponse(
//                processUseCase.execute(req, principal.getName()),
//                new TypeReference<>() {});
//    }

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
//    @PostMapping("/submit")
//    public ApiResponse<String> submitForApproval(@RequestBody SubmitRequest req) {
//        return ResponseUtils.createSuccessResponse(
//                submitUseCase.execute(req.getRecordIds()),
//                new TypeReference<>() {}
//        );
//    }


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


// UPDATE these endpoints with role-based security:

    // SUBMIT: DRAFT → PENDING (HR only)
    @PostMapping("/submit")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<String> submitForApproval(@RequestBody SubmitRequest req) {
        return ResponseUtils.createSuccessResponse(
                submitUseCase.execute(req.getRecordIds()),
                new TypeReference<>() {}
        );
    }

    // APPROVE: PENDING → APPROVED (Manager only)
    @PostMapping("/approve")
    @PreAuthorize("hasRole('MANAGER') or hasRole('Admin')")
    public ApiResponse<String> approve(
            @RequestBody ApproveRequest req,
            Principal principal) {
        return ResponseUtils.createSuccessResponse(
                approveUseCase.execute(req.getYearMonth(), req.getRecordIds(), principal.getName()),
                new TypeReference<>() {});
    }

    // PROCESS: APPROVED → PROCESSED (Finance only)
    @PostMapping("/process")
    @PreAuthorize("hasRole('FINANCE') or hasRole('Admin')")
    public ApiResponse<String> process(
            @RequestBody ProcessPayrollRequest req,
            Principal principal) {
        return ResponseUtils.createSuccessResponse(
                processUseCase.execute(req, principal.getName()),
                new TypeReference<>() {});
    }

    // GENERATE: Create DRAFT records (HR only)
    @PostMapping("/generate")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<String> generate(@RequestBody BulkGenerateRequest req) {
        return ResponseUtils.createSuccessResponse(bulkGenerateUseCase.execute(req), new TypeReference<>() {});
    }





// In PayrollController.java, make sure ALL these methods match the service:


    // GET all configs as map
    @GetMapping("/config")
    public ApiResponse<Map<String, Double>> getConfigMap() {
        return ResponseUtils.createSuccessResponse(
                configService.getAllConfigValues(),
                new TypeReference<>() {}
        );
    }

    // GET all configs as list (for admin page)
    @GetMapping("/config/all")
    public ApiResponse<List<SalaryConfiguration>> getAllConfigs() {
        return ResponseUtils.createSuccessResponse(
                configService.getAllConfigs(),
                new TypeReference<>() {}
        );
    }

    // GET single config by key
    @GetMapping("/config/{key}")
    public ApiResponse<Double> getConfigValue(@PathVariable String key) {
        return ResponseUtils.createSuccessResponse(
                configService.getValue(key),
                new TypeReference<>() {}
        );
    }

    // CREATE
    @PostMapping("/config")
    public ApiResponse<SalaryConfiguration> createConfig(@RequestBody SalaryConfiguration config) {
        return ResponseUtils.createSuccessResponse(
                configService.create(config),
                new TypeReference<>() {}
        );
    }

    // UPDATE by ID
    @PutMapping("/config/{id}")
    public ApiResponse<SalaryConfiguration> updateConfig(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        return ResponseUtils.createSuccessResponse(
                configService.update(id, body.get("configValue")),
                new TypeReference<>() {}
        );
    }

    // UPDATE by KEY (easiest - used by frontend config panel)
    @PutMapping("/config/key/{key}")
    public ApiResponse<SalaryConfiguration> updateConfigByKey(
            @PathVariable String key,
            @RequestBody Map<String, Double> body) {
        return ResponseUtils.createSuccessResponse(
                configService.updateByKey(key, body.get("configValue")),
                new TypeReference<>() {}
        );
    }

    // UPDATE full object
    @PutMapping("/config/full/{id}")
    public ApiResponse<SalaryConfiguration> updateFullConfig(
            @PathVariable Long id,
            @RequestBody SalaryConfiguration config) {
        return ResponseUtils.createSuccessResponse(
                configService.updateFull(id, config),
                new TypeReference<>() {}
        );
    }

    // TOGGLE status (instead of delete)
    @PutMapping("/config/{id}/toggle-status")
    public ApiResponse<String> toggleConfigStatus(@PathVariable Long id) {
        configService.toggleStatus(id);
        return ResponseUtils.createSuccessResponse(
                "Configuration status toggled",
                new TypeReference<>() {}
        );
    }

    // BULK UPDATE
    @PutMapping("/config/bulk")
    public ApiResponse<List<SalaryConfiguration>> bulkUpdateConfigs(
            @RequestBody Map<String, Double> updates) {
        return ResponseUtils.createSuccessResponse(
                configService.bulkUpdate(updates),
                new TypeReference<>() {}
        );
    }

}