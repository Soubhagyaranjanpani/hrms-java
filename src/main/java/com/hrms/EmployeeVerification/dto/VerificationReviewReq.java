
package com.hrms.EmployeeVerification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerificationReviewReq {

    @NotNull
    private String decision;

    private String rejectionReason;
}
