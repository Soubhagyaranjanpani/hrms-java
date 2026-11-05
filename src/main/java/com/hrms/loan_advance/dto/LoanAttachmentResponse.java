package com.hrms.loan_advance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** Result of uploadAttachment. Send `attachment` in submitLoanApplication. */
@Getter
@Setter
@AllArgsConstructor
public class LoanAttachmentResponse {
    private String attachment;   // stored key, goes into loan.attachment
    private String fileName;     // original name, for display
    private long sizeBytes;
}