package com.hrms.employee.dto;

import com.hrms.employee.domain.Employee;
import lombok.Data;

import java.time.LocalDate;
@Data
public class EmployeeCertificationResponse {
    private Long id;
    private String certificateName;
    private String issueAuthority;
    private String certificateNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Integer expiryReminderDays;
    private String notes;
    private Long employeeId;
    private String employeeName;
//    private String department;
//    private String designation;
//    private Integer totalCart;

}
