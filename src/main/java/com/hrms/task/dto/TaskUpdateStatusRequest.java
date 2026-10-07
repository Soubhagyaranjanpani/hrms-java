package com.hrms.task.dto;

import lombok.Data;

@Data
public class TaskUpdateStatusRequest {

    private Long taskId;
    private String status;
    private Integer progress;
}
