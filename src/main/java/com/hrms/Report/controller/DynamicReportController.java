package com.hrms.Report.controller;




import com.hrms.Report.dto.ReportRequestDTO;
import com.hrms.Report.dto.ReportResponseDTO;
import com.hrms.Report.service.DynamicReportConfigService;
import com.hrms.Report.service.DynamicReportService;
import com.hrms.Report.service.ExcelExportService;
import com.hrms.Report.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class DynamicReportController {

    private final DynamicReportConfigService configService;
    private final DynamicReportService reportService;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;

    @GetMapping("/types")
    public ResponseEntity<?> getReportTypes() {
        return ResponseEntity.ok(configService.discoverReportTypes());
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@RequestBody ReportRequestDTO request) {
        try {
            ReportResponseDTO report = reportService.generateDynamicReport(request);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/download/excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody ReportRequestDTO request) {
        try {
            ReportResponseDTO data = reportService.generateDynamicReport(request);
            byte[] excel = excelExportService.exportToExcel(data);
            String filename = request.getReportType() + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody ReportRequestDTO request) {
        try {
            ReportResponseDTO data = reportService.generateDynamicReport(request);
            byte[] pdf = pdfExportService.exportToPdf(data);
            String filename = request.getReportType() + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
