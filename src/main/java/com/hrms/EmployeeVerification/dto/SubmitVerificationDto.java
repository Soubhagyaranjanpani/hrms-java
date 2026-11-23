
package com.hrms.EmployeeVerification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SubmitVerificationDto {

    @NotBlank(message = "Client name is required")
    private String clientName;

    private String projectName;

    @NotBlank(message = "Site location is required")
    private String siteLocation;

    private String contactPerson;

    @NotBlank(message = "Purpose description is required")
    private String purposeDescription;

    @NotEmpty(message = "Select at least one date")
    private List<@NotNull LocalDate> dates;
}
