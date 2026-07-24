package com.hrms.appointment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AppointmentRecordResponse {

    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;

    private String appointmentOrderNumber;
    private LocalDate appointmentDate;

    private String appointmentAuthority;             // person's name
    private String appointmentAuthorityDesignation;   // role, e.g. "HR Director"

    private String appointmentType;
    private String employmentType;

    private String designation;   // initial designation name
    private String department;    // initial department name
    private String branch;        // initial branch name

    private LocalDate joiningDate;
    private Integer probationPeriodMonths;
    private LocalDate confirmationDueDate;

    private Boolean isActive;

    private String documentPath;
    private String documentName;

    private String remarks;
    private String processedBy;
}
