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
public class InterviewTypeResponse {
    private Long id;
    private String interviewTypeCode;
    private String interviewTypeName;
    private String description;
    private String status;
    private LocalDateTime lastChangeAt;
    private String lastChangeBy;
    private Long interviewModeId;
}
