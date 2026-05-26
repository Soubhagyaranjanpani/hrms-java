package com.hrms.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public  class ChangeRequestResponse {
    private Long id;
    private String reason;
    private LocalDateTime requestedDeadline;
    private String status;      // PENDING, APPROVED, REJECTED
    private LocalDateTime createdAt;
}

