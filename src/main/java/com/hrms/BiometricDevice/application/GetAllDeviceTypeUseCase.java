package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.DeviceType;
import com.hrms.BiometricDevice.dto.DeviceTypeResponse;
import com.hrms.BiometricDevice.infrastructure.DeviceTypeInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllDeviceTypeUseCase {

    private final DeviceTypeInfrastructure repository;

    public List<DeviceTypeResponse> execute(Integer flag) {

        List<DeviceType> deviceTypes;

        if (flag != null && flag == 1) {

            deviceTypes = repository.findByIsActiveTrue();

        } else {

            deviceTypes = repository.findAll();
        }

        return deviceTypes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private DeviceTypeResponse mapToResponse(
            DeviceType deviceType) {

        return new DeviceTypeResponse(
                deviceType.getId(),
                deviceType.getDeviceTypeCode(),
                deviceType.getDeviceTypeName(),
                deviceType.getIsActive()
        );
    }
}
