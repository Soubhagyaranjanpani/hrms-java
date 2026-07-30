package com.hrms.deputation.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CreateDeputationRequest {
    private Long employeeId;
    private String deputationOrderNumber;
    private String deputationOrganization;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long deputationTypeId;      // ✅ Dropdown 1
    private Long reportingAuthorityId;   // ✅ Dropdown 2
    private String remarks;
}