package com.hrms.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskChangeRequestDto {
    private String reason;
    private LocalDateTime requestedDeadline;   // optional
}