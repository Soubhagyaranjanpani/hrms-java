package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.BiometricDevice;
import com.hrms.BiometricDevice.infrastructure.BiometricDeviceInfrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusBiometricDeviceUseCase {

    private final BiometricDeviceInfrastructure repository;

    public BiometricDevice execute(
            Long id,
            Boolean status) {

        BiometricDevice device =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Biometric device not found"));

        device.setIsActive(status);

        return repository.save(device);
    }
}
