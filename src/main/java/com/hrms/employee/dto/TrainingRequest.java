package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingRequest {

    private Long employeeId;
    private String trainingName;
    private LocalDate completedOn;
}
