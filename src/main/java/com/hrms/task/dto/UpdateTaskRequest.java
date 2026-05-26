package com.hrms.task.dto;



import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateTaskRequest {

    private String title;
    private String description;
    private String priority;        // LOW, MEDIUM, HIGH, CRITICAL
    private Long assignedToId;
    private Long departmentId;
    private Long branchId;          // if your system has branches
    private LocalDateTime dueDate;
    private String effort;
    private String tags;
    private Integer progress;       // optional manual progress update
}
