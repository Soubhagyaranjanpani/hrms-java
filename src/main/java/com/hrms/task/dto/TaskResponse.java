package com.hrms.task.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Integer progress;
    private String effort;
    private String tags;

    private String assignedTo;
    private Long assignedToId;
    private String assignedBy;
    private Long assignedById;
    private String createdByName;

    private String department;
    private Long departmentId;

    // ✅ Branch information
    private Long branchId;
    private String branchName;


    private LocalDateTime dueDate;
    private LocalDateTime startDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer estimatedHours;
    private Integer actualHours;

    private Long parentTaskId;
    private String parentTaskTitle;

    private List<CommentResponse> comments;
    private List<TaskResponse> subtasks;
    private ChangeRequestResponse changeRequest;
}