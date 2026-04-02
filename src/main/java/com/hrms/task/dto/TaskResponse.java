package com.hrms.task.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {

    private Long id;
    private String title;
    private String priority;
    private String status;

    private String assignedTo;
    private String assignedBy;

    private LocalDateTime dueDate;
}
