package com.hrms.transfer.dto;

import com.hrms.master.dto.TransferTypeResponse;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TransferRecordResponse {

    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;
    private String designation;

    private String transferOrderNumber;
    private LocalDate transferDate;

    // ✅ Changed: Full TransferType object in response
    private TransferTypeResponse transferType;

    private String fromDepartment;
    private String toDepartment;
    private String fromBranch;
    private String toBranch;

    private LocalDate effectiveDate;
    private String transferReason;

    private Boolean isActive;

    private String documentPath;
    private String documentName;

    private String processedBy;
}