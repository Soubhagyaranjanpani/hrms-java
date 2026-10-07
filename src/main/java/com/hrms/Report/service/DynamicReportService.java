package com.hrms.Report.service;




import com.hrms.Report.dto.ReportRequestDTO;
import com.hrms.Report.dto.ReportResponseDTO;
import com.hrms.Report.dto.ReportSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicReportService {

    private final DynamicReportDataService dataService;
    private final DynamicReportConfigService configService;

    public ReportResponseDTO generateDynamicReport(ReportRequestDTO request) {
        List<Map<String, Object>> rawData = dataService.fetchReportData(request);
        List<Map<String, Object>> configColumns = getColumnsForReportType(request.getReportType());

        if (request.getSelectedColumns() != null && !request.getSelectedColumns().isEmpty()) {
            rawData = filterColumns(rawData, request.getSelectedColumns());
            configColumns = configColumns.stream()
                    .filter(col -> request.getSelectedColumns().contains(col.get("field")))
                    .collect(Collectors.toList());
        }

        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            rawData = sortData(rawData, request.getSortBy(), request.getSortDirection());
        }

        List<ReportResponseDTO.ColumnDefinition> columns = configColumns.stream()
                .map(this::convertToColumnDefinition)
                .collect(Collectors.toList());

        ReportSummaryDTO summary = calculateSummary(rawData, columns);

        return ReportResponseDTO.builder()
                .reportType(request.getReportType())
                .reportName(getReportName(request.getReportType()))
                .generatedAt(LocalDateTime.now())
                .summary(summary)
                .data(rawData)
                .columns(columns)
                .build();
    }

    private List<Map<String, Object>> getColumnsForReportType(String reportType) {
        String entityClassName = switch (reportType) {
            case "EMPLOYEE_REPORT" -> "com.hrms.employee.domain.Employee";
            case "ATTENDANCE_REPORT" -> "com.hrms.attendance.domain.Attendance";
            case "LEAVE_REPORT" -> "com.hrms.leave.domain.Leave";
            case "PAYROLL_REPORT" -> "com.hrms.payroll.domain.PayrollRecord";
            case "TASK_REPORT" -> "com.hrms.task.domain.Task";
            case "DEPARTMENT_REPORT" -> "com.hrms.master.domain.Department";
            case "BRANCH_REPORT" -> "com.hrms.master.domain.Branch";
            default -> throw new IllegalArgumentException("Unknown report type: " + reportType);
        };
        try {
            return configService.getEntityColumns(Class.forName(entityClassName));
        } catch (ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> filterColumns(List<Map<String, Object>> data, List<String> columns) {
        return data.stream().map(row -> {
            Map<String, Object> filtered = new LinkedHashMap<>();
            columns.forEach(col -> { if (row.containsKey(col)) filtered.put(col, row.get(col)); });
            return filtered;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> sortData(List<Map<String, Object>> data, String sortBy, String direction) {
        if (data.isEmpty()) return data;
        Comparator<Map<String, Object>> comparator = (a, b) -> {
            Object va = a.get(sortBy), vb = b.get(sortBy);
            if (va == null && vb == null) return 0;
            if (va == null) return 1;
            if (vb == null) return -1;
            if (va instanceof Comparable && vb instanceof Comparable)
                return ((Comparable) va).compareTo(vb);
            return va.toString().compareTo(vb.toString());
        };
        if ("DESC".equalsIgnoreCase(direction)) comparator = comparator.reversed();
        return data.stream().sorted(comparator).collect(Collectors.toList());
    }

    private ReportResponseDTO.ColumnDefinition convertToColumnDefinition(Map<String, Object> colConfig) {
        return ReportResponseDTO.ColumnDefinition.builder()
                .field((String) colConfig.get("field"))
                .header((String) colConfig.get("header"))
                .type((String) colConfig.get("type"))
                .sortable((Boolean) colConfig.getOrDefault("sortable", true))
                .filterable((Boolean) colConfig.getOrDefault("filterable", true))
                .build();
    }

    private ReportSummaryDTO calculateSummary(List<Map<String, Object>> data,
                                              List<ReportResponseDTO.ColumnDefinition> columns) {
        Map<String, Long> statusCounts = new HashMap<>();
        Map<String, Object> totals = new HashMap<>();
        Map<String, Object> averages = new HashMap<>();

        ReportResponseDTO.ColumnDefinition statusCol = columns.stream()
                .filter(col -> "STATUS".equals(col.getType())).findFirst().orElse(null);

        if (statusCol != null) {
            String sf = statusCol.getField();
            statusCounts = data.stream()
                    .filter(row -> row.containsKey(sf) && row.get(sf) != null)
                    .collect(Collectors.groupingBy(row -> row.get(sf).toString(), Collectors.counting()));
        }

        for (ReportResponseDTO.ColumnDefinition col : columns) {
            if ("NUMBER".equals(col.getType()) || "CURRENCY".equals(col.getType())) {
                String f = col.getField();
                double sum = data.stream()
                        .filter(row -> row.get(f) instanceof Number)
                        .mapToDouble(row -> ((Number) row.get(f)).doubleValue()).sum();
                totals.put(f + "Total", sum);
                averages.put(f + "Average", data.isEmpty() ? 0 : Math.round(sum / data.size() * 100.0) / 100.0);
            }
        }

        return ReportSummaryDTO.builder()
                .totalRecords(data.size()).statusCounts(statusCounts)
                .totals(totals).averages(averages).build();
    }

    private String getReportName(String reportType) {
        return reportType.replace("_", " ").toLowerCase().replace("report", "Report").trim();
    }
}
