package com.hrms.BiometricDevice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeResponse {

    private Long id;

    private String deviceTypeCode;

    private String deviceTypeName;

    private Boolean isActive;
}
