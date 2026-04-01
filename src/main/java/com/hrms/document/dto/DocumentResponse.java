package com.hrms.document.dto;

import lombok.Data;

@Data
public class DocumentResponse {

    private Long id;
    private String fileName;
    private String category;
    private String documentType;
}
