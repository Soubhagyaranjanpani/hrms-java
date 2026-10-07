package com.hrms.master.dto;

import lombok.Data;

@Data
public class RetirementTypeResponse {
    private Long id;
    private String name;
    private Boolean isActive;
}