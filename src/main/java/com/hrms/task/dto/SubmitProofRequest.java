package com.hrms.task.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** Sent as multipart/form-data. */
@Data
public class SubmitProofRequest {
    private Integer progress;          // 0-100
    private String note;
    private Double hoursSpent;
    private String proofLink;          // optional link (GitHub, Drive, etc.)
    private Boolean finalSubmission = false;
    private MultipartFile file;        // optional proof file
}
