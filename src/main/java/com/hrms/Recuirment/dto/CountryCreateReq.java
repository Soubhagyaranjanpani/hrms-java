package com.hrms.Recuirment.dto;

import lombok.Data;

@Data
public class CountryCreateReq {
    private String countryCode;
    private String countryName;
    private String status;

}
