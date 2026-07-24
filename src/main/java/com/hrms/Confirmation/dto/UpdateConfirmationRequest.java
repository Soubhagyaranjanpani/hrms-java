package com.hrms.confirmation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateConfirmationRequest {

    private String confirmationOrderNumber;
    private LocalDate confirmationDate;
    private String remarks;
}
