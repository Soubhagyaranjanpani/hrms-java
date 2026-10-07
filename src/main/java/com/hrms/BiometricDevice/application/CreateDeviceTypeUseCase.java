package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.DeviceType;
import com.hrms.BiometricDevice.dto.DeviceTypeRequest;
import com.hrms.BiometricDevice.infrastructure.DeviceTypeInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateDeviceTypeUseCase {

    private final DeviceTypeInfrastructure repository;

    public DeviceType execute(DeviceTypeRequest request) {

        // Check duplicate code
        if (repository.existsByDeviceTypeCode(
                request.getDeviceTypeCode())) {

            throw new RuntimeException(
                    "Device type code already exists"
            );
        }

        // Check duplicate name
        if (repository.existsByDeviceTypeName(
                request.getDeviceTypeName())) {

            throw new RuntimeException(
                    "Device type name already exists"
            );
        }

        // Create entity
        DeviceType deviceType = new DeviceType();

        deviceType.setDeviceTypeCode(
                request.getDeviceTypeCode()
        );

        deviceType.setDeviceTypeName(
                request.getDeviceTypeName()
        );

        deviceType.setIsActive(true);

        // Save
        return repository.save(deviceType);
    }
}
