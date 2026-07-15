package com.hrms.serviceBook.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceBookRequest {
    @NotNull(message = "employeeId is required")
    private Long employeeId;

    private String serviceName;
}
