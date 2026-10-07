package com.hrms.Recuirment.dto;

import lombok.Data;

@Data
public class OfferStatusCreateReq {
    private String statusCode;
    private String statusName;
    private String description;

}
