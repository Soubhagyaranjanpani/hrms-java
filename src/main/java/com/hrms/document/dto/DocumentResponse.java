package com.hrms.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Employee document details")
public class DocumentResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "degree.pdf")
    private String fileName;

    @Schema(example = "/uploads/degree.pdf")
    private String filePath;

    @Schema(example = "application/pdf")
    private String fileType;

    @Schema(example = "EDUCATION")
    private String category;

    @Schema(example = "DEGREE_CERTIFICATE")
    private String documentType;

    @Schema(example = "true")
    private Boolean isVerified;

    @Schema(example = "2026-04-01T10:15:30")
    private LocalDateTime uploadedAt;



        private String uploadedBy;
        private Long fileSize;


}
