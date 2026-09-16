package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.BiometricDevice;
import com.hrms.BiometricDevice.dto.BiometricDeviceResponse;
import com.hrms.BiometricDevice.infrastructure.BiometricDeviceInfrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllBiometricDeviceUseCase {

    private final BiometricDeviceInfrastructure repository;

    public List<BiometricDeviceResponse> execute(Integer flag) {

        List<BiometricDevice> data;

        if (flag != null && flag == 1) {

            data = repository.findByIsActiveTrue();

        } else {

            data = repository.findAll();
        }

        return data.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private BiometricDeviceResponse mapToResponse(
            BiometricDevice device) {

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
