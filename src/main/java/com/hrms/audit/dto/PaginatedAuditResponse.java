package com.hrms.audit.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaginatedAuditResponse {
    private List<AuditLogResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;

    public PaginatedAuditResponse(List<AuditLogResponse> content, int pageNumber,
                                  int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.last = pageNumber >= totalPages - 1;
        this.first = pageNumber == 0;
    }
}