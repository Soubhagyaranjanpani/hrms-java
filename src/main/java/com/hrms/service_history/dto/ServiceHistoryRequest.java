package com.hrms.service_history.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ServiceHistoryRequest {
    private Long serviceBookId;
    private Long employeeId;
    private String eventType;
    private String fromDesignation;
    private String toDesignation;
    private String fromBranch;
    private String toBranch;
    private LocalDate eventDate;
    private String remarks;
}