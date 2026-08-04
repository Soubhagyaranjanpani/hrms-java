package com.hrms.training.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateTrainingRequest {
    private List<TrainingItemRequest> trainings;
}
