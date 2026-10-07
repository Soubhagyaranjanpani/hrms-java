package com.hrms.training.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingRecordResponse {

    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;

    private String department; // snapshot
    private String designation; // snapshot

    private String trainingName;
    private String provider;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer hours;
    private String certification;

    private Boolean isActive;

    private String documentPath;
    private String documentName;
}
