package com.hrms.Appointment_Type.dto;

import lombok.Data;

@Data
public class AppointmentTypeRequest {

    private String appointmentType;
    private String createdBy;
}