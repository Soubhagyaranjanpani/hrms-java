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
public class StatusCategoryResponse {
    private Long id;
    private String categoryName;
    private String status;
    private LocalDateTime lastChangeAt;
    private String lastChangeBy;
}
