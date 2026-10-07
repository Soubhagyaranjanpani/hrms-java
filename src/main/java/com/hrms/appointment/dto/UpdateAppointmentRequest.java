package com.hrms.appointment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateAppointmentRequest {

    private String appointmentOrderNumber;
    private LocalDate appointmentDate;

    private Long appointmentTypeId;
    private Long employmentTypeId;

    private LocalDate joiningDate;
    private Integer probationPeriodMonths;

    private String remarks;

    // Designation/Department/Branch/Authority intentionally left out of the
    // partial-update flow, mirroring UpdatePromotionRecordUseCase — wire these
    // in the same way (uncomment + resolve via repositories) if you want them editable.
}