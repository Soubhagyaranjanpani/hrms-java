package com.hrms.appointment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateAppointmentRequest {

    private Long employeeId;

    private String appointmentOrderNumber;
    private LocalDate appointmentDate;

    private Long appointmentAuthorityId;

    private String appointmentType;   // Permanent / Contract / Temporary
    private String employmentType;    // Full-Time / Part-Time / Contractual

    private Long initialDesignationId;
    private Long initialDepartmentId;
    private Long initialBranchId;

    private LocalDate joiningDate;
    private Integer probationPeriodMonths; // defaults to 6 in the use case if not supplied

    private String remarks;
}
