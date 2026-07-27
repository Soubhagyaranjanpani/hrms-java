package com.hrms.deputation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateDeputationRequest {

    private String deputationOrderNumber;
    private String deputationOrganization;
    private LocalDate startDate;
    private LocalDate endDate;
    private String deputationType;
}
