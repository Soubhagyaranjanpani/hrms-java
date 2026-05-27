package com.hrms.Report.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDTO {
    private long totalRecords;
    private Map<String, Long> statusCounts;
    private Map<String, Object> totals;
    private Map<String, Object> averages;
}
