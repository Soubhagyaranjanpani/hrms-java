package com.hrms.Report.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    private String reportType;
    private String reportName;
    private LocalDateTime generatedAt;
    private ReportSummaryDTO summary;
    private List<java.util.Map<String, Object>> data;
    private List<ColumnDefinition> columns;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnDefinition {
        private String field;
        private String header;
        private String type;
        private boolean sortable;
        private boolean filterable;
    }
}
