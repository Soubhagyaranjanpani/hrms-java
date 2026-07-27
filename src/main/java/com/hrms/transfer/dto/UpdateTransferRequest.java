package com.hrms.transfer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTransferRequest {

    private String transferOrderNumber;
    private LocalDate transferDate;
    private String transferType;
    private LocalDate effectiveDate;
    private String transferReason;

    // fromDepartment/toDepartment/fromBranch/toBranch intentionally left out of the
    // partial-update flow, mirroring UpdatePromotionRecordUseCase — wire these
    // in the same way if you want them editable later.
}
