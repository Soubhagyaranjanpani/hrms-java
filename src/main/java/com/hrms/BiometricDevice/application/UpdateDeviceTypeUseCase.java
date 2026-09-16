package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.DeviceType;
import com.hrms.BiometricDevice.dto.DeviceTypeRequest;
import com.hrms.BiometricDevice.infrastructure.DeviceTypeInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDeviceTypeUseCase {

    private final DeviceTypeInfrastructure repository;

    public DeviceType execute(
            Long id,
            DeviceTypeRequest request) {

        // Find existing record
        DeviceType deviceType = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Device type not found"
                        )
                );

        // Update fields
        deviceType.setDeviceTypeCode(
                request.getDeviceTypeCode()
        );

        deviceType.setDeviceTypeName(
                request.getDeviceTypeName()
        );

        // Save updated record
        return repository.save(deviceType);
    }
}
