package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.BiometricDevice;
import com.hrms.BiometricDevice.domain.DeviceType;
import com.hrms.BiometricDevice.domain.Manufacturer;
import com.hrms.BiometricDevice.dto.BiometricDeviceRequest;
import com.hrms.BiometricDevice.dto.BiometricDeviceResponse;
import com.hrms.BiometricDevice.infrastructure.BiometricDeviceInfrastructure;
import com.hrms.BiometricDevice.infrastructure.DeviceTypeInfrastructure;
import com.hrms.BiometricDevice.infrastructure.ManufacturerInfrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateBiometricDeviceUseCase {

    private final BiometricDeviceInfrastructure repository;

    private final DeviceTypeInfrastructure deviceTypeRepository;

    private final ManufacturerInfrastructure manufacturerRepository;

    public BiometricDeviceResponse execute(
            BiometricDeviceRequest request) {

        // 1. Check duplicate device code
        if (repository.existsByDeviceCode(
                request.getDeviceCode())) {

            throw new RuntimeException(
                    "Device code already exists");
        }

        // 2. Check duplicate serial number
        if (request.getSerialNumber() != null
                && !request.getSerialNumber().isBlank()
                && repository.existsBySerialNumber(
                request.getSerialNumber())) {

            throw new RuntimeException(
                    "Serial number already exists");
        }

        // 3. Find Device Type
        DeviceType deviceType =
                deviceTypeRepository.findById(
                        request.getDeviceTypeId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Device type not found"));

        // 4. Find Manufacturer
        Manufacturer manufacturer =
                manufacturerRepository.findById(
                        request.getManufacturerId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Manufacturer not found"));

        // 5. Create Biometric Device
        BiometricDevice device =
                new BiometricDevice();

        device.setDeviceCode(
                request.getDeviceCode());

        device.setDeviceName(
                request.getDeviceName());

        device.setDeviceType(deviceType);

        device.setManufacturer(manufacturer);

        device.setModel(
                request.getModel());

        device.setSerialNumber(
                request.getSerialNumber());

        device.setIpAddress(
                request.getIpAddress());

        device.setPort(
                request.getPort());

        device.setIsActive(true);

        // 6. Save
        BiometricDevice savedDevice =
                repository.save(device);

        // 7. Convert Entity -> Response DTO
        return new BiometricDeviceResponse(
                savedDevice.getId(),
                savedDevice.getDeviceCode(),
                savedDevice.getDeviceName(),

                savedDevice.getDeviceType().getId(),
                savedDevice.getDeviceType().getDeviceTypeName(),

                savedDevice.getManufacturer().getId(),
                savedDevice.getManufacturer().getManufacturerName(),

                savedDevice.getModel(),
                savedDevice.getSerialNumber(),
                savedDevice.getIpAddress(),
                savedDevice.getPort(),
                savedDevice.getIsActive()
        );
    }
}