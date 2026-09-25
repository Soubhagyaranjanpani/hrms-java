package com.hrms.Recuirment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateStatusResponse {
    private Long id;
    private String statusCode;
    private String statusName;
    private Integer displayOrder;
    private String finalStatus;
    private String statusColor;
    private String description;
    private String status;
    private LocalDateTime lastChangeAt;
    private String lastChangeBy;
    private Long statusCategoryId;
}
