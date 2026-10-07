package com.hrms.Recuirment.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CityCreateReq {
    private String cityCode;
    private String cityName;
    private String status;
    private Long stateId;




}
