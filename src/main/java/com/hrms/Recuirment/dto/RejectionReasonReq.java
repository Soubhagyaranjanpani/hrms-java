package com.hrms.Recuirment.dto;

import com.hrms.Recuirment.domain.ReasonCategory;
import lombok.Data;

@Data
public class RejectionReasonReq {
    private String reasonCode;
    private String reasonName;
    private String description;
    private ReasonCategory reasonCategory;
    private String status;
    private Long reasonCategoryId;
}
