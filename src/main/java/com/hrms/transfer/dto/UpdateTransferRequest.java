package com.hrms.transfer.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateTransferRequest {
    private String transferOrderNumber;
    private LocalDate transferDate;

    // ✅ Changed: Use ID instead of String
    private Long transferTypeId;

    private Long toDepartmentId;
    private Long toBranchId;
    private LocalDate effectiveDate;
    private String transferReason;
}