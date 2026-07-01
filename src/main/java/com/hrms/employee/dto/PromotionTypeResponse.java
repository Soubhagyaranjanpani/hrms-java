package com.hrms.employee.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionTypeResponse {

    private Long id;
    private String promotionTypeName;
    private String description;
    private Boolean status;
}