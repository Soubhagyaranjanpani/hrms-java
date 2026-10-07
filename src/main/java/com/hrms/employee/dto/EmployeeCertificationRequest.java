package com.hrms.employee.dto;

import com.hrms.employee.domain.Employee;
import lombok.Data;

import java.time.LocalDate;
@Data
public class EmployeeCertificationRequest {
    private String certificateName;
    private String issueAuthority;
    private String certificateNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Integer expiryReminderDays;
    private String notes;
    private Long employeeId;

}
