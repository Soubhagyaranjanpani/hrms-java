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
public class ReasonCategoryResponse {

    private Long id;
    private String reasonCategoryName;
    private String status;
    private LocalDateTime lastChangeAt;
    private String lastChangeBy;
}
