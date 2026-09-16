package com.hrms.BiometricDevice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BiometricDeviceResponse {

    private Long id;

    private String deviceCode;

    private String deviceName;

    private Long deviceTypeId;

    private String deviceTypeName;

    private Long manufacturerId;

    private String manufacturerName;

    private String model;

    private String serialNumber;

    private String ipAddress;

    private Integer port;

    private Boolean isActive;
}