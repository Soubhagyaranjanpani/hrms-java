package com.hrms.Recuirment.dto;

import lombok.Data;

@Data
public class StateCreateReq {
    private String stateCode;
    private String stateName;
    private String status;
    private Long countryId;
}
