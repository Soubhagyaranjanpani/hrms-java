package com.hrms.employee.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EmployeeConfirmationDocumentRequest {

    private Long employeeId;
    private String fileName;
    private MultipartFile file;
}