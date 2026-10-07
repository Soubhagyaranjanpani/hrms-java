package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingDto {
    private String trainingName;
    private LocalDate completedOn;
}
