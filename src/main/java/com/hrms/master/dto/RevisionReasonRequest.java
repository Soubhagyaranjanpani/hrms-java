package com.hrms.master.dto;

import lombok.Data;

@Data
public class RevisionReasonRequest {
    private Long id;
    private String name;
    private Boolean isActive;
}