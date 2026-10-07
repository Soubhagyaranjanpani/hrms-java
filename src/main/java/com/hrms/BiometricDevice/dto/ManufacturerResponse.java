package com.hrms.BiometricDevice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerResponse {

    private Long id;

    private String manufacturerCode;

    private String manufacturerName;

    private Boolean isActive;
}
