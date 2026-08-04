package com.hrms.training.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTrainingRequest {

    private String trainingName;
    private String provider;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer hours;
    private String certification;
}
