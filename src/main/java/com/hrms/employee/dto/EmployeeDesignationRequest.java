package com.hrms.employee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeDesignationRequest {

    // id removed: update() now takes the id from the path variable (PUT /{id}),
    // not the request body, to avoid path/body id mismatches.

    @NotNull(message = "employeeId is required")
    private Long employeeId;

    @NotNull(message = "designationId is required")
    private Long designationId;
}