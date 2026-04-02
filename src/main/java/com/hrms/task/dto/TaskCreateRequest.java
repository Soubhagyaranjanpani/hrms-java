package com.hrms.task.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {

    private String title;
    private String description;
    private String priority;

    private Long assignedToId;
    private Long departmentId;

    private LocalDateTime startDate;
    private LocalDateTime dueDate;

    private Integer estimatedHours;
}
