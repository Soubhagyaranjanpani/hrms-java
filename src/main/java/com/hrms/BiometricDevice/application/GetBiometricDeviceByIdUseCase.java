package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.BiometricDevice;
import com.hrms.BiometricDevice.dto.BiometricDeviceResponse;
import com.hrms.BiometricDevice.infrastructure.BiometricDeviceInfrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBiometricDeviceByIdUseCase {

    private final BiometricDeviceInfrastructure repository;

    public BiometricDeviceResponse execute(Long id) {

        BiometricDevice device =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Biometric device not found"));

        return new BiometricDeviceResponse(

                device.getId(),

                device.getDeviceCode(),

                device.getDeviceName(),

                device.getDeviceType().getId(),

                device.getDeviceType().getDeviceTypeName(),

                device.getManufacturer().getId(),

                device.getManufacturer().getManufacturerName(),

                device.getModel(),

                device.getSerialNumber(),

                device.getIpAddress(),

                device.getPort(),

                device.getIsActive()
        );
    }
}
