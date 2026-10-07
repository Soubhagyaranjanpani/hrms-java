package com.hrms.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeConfirmationDocumentResponse {

    private Long id;

    private String originalFileName;

    private String storedFileName;

    private String contentType;

    private Long employeeConfirmationId;

    private Boolean isDeleted;
}