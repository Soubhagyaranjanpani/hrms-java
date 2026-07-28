package com.hrms.master.dto;

import lombok.Data;

@Data
public class RevisionReasonResponse {
    private Long id;
    private String name;
    private Boolean isActive;
}