package com.hrms.Appointment_Type.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentTypeResponse {

    private Long id;
    private String appointmentType;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}