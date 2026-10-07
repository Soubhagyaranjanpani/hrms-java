package com.hrms.employee.dto;

import lombok.Data;

@Data
public class QualificationRequest {

    private Long employeeId;
    private String degree;
    private String institution;
    private Integer year;
}
