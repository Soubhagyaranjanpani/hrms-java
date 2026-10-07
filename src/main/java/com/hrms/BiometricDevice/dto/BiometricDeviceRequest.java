package com.hrms.BiometricDevice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiometricDeviceRequest {

    private String deviceCode;

    private String deviceName;

    private Long deviceTypeId;

    private Long manufacturerId;

    private String model;

    private String serialNumber;

    private String ipAddress;

    private Integer port;
}