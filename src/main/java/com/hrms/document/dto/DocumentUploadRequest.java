package com.hrms.document.dto;

import lombok.Data;

@Data
public class DocumentUploadRequest {

    private Long employeeId;
    private String category; // EDUCATION, ID_PROOF
}
