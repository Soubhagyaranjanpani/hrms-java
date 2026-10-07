package com.hrms.master.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RevisionReasonResponse {
    private Long id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
}