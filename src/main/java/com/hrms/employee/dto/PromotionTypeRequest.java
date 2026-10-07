package com.hrms.employee.dto;

import lombok.Data;

@Data
public class PromotionTypeRequest {

    private String promotionTypeName;
    private String description;
    private Boolean status;
}