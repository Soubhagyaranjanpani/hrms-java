package com.hrms.retirement.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateRetirementRequest {

    private LocalDate retirementDate;
    private String pensionNumber;
    private String retirementOrder;
    private String retirementBenefits;
}
