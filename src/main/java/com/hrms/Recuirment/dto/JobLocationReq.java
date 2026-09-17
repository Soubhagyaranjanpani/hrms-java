package com.hrms.Recuirment.dto;

import lombok.Data;

@Data
public class JobLocationReq {
    private String locationCode;
    private String locationName;
    private String pinCode;
    private String description;
    private String status;
    private Long branchId;
    private Long countryId;
    private Long stateId;
    private Long cityId;
    private Long workModeId;
}
