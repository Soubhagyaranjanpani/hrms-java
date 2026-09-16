package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.DeviceType;
import com.hrms.BiometricDevice.infrastructure.DeviceTypeInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StatusDeviceTypeUseCase {

    private final DeviceTypeInfrastructure repository;

    public DeviceType execute(
            Long id,
            Boolean status) {

        DeviceType deviceType = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Device type not found"
                        )
                );

        deviceType.setIsActive(status);

        return repository.save(deviceType);
    }
}
