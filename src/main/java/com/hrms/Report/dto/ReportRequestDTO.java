package com.hrms.Report.dto;


import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class ReportRequestDTO {
    private String reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> statuses;
    private List<Long> ids;
    private String month;
    private String sortBy;
    private String sortDirection = "DESC";
    private List<String> selectedColumns;
    private String searchTerm;
    private Map<String, Object> additionalFilters;
}
