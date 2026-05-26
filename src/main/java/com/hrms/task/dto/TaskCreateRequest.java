package com.hrms.task.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private String priority;        // LOW, MEDIUM, HIGH, CRITICAL
    private Long assignedToId;
    private Long departmentId;
    private Long branchId;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private Integer estimatedHours;
    private String effort;          // e.g. "3 days"
    private String tags;            // comma-separated
    private Long parentTaskId;
}