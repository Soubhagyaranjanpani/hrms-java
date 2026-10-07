package com.hrms.Recuirment.dto;

import com.hrms.Recuirment.domain.ReasonCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectionReasonResponse {
    private Long id;
    private String reasonCode;
    private String reasonName;
    private String description;
    private ReasonCategory reasonCategory;
    private Long reasonCategoryId;
    private String status;
    private LocalDateTime lastChangeAt;
    private String lastChangeBy;
}
