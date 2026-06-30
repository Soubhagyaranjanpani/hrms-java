package com.hrms.employee.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeConfirmationRequest {

    private Long employeeId;

    private String confirmationOrderNumber;

    private LocalDate confirmationDate;

    // Dropdown se Appointment Authority ki ID aayegi
    private Long confirmedById;

    private String remarks;
}