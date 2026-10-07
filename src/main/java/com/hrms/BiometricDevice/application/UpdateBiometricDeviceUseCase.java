package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.BiometricDevice;
import com.hrms.BiometricDevice.domain.DeviceType;
import com.hrms.BiometricDevice.domain.Manufacturer;
import com.hrms.BiometricDevice.dto.BiometricDeviceRequest;
import com.hrms.BiometricDevice.infrastructure.BiometricDeviceInfrastructure;
import com.hrms.BiometricDevice.infrastructure.DeviceTypeInfrastructure;
import com.hrms.BiometricDevice.infrastructure.ManufacturerInfrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateBiometricDeviceUseCase {

    private final BiometricDeviceInfrastructure repository;

    private final DeviceTypeInfrastructure deviceTypeRepository;

    private final ManufacturerInfrastructure manufacturerRepository;

    public BiometricDevice execute(
            Long id,
            BiometricDeviceRequest request) {

        // 1. Find existing device

        BiometricDevice device =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Biometric device not found"));


        // 2. Find Device Type

        DeviceType deviceType =
                deviceTypeRepository.findById(
                        request.getDeviceTypeId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Device type not found"));


        // 3. Find Manufacturer

        Manufacturer manufacturer =
                manufacturerRepository.findById(
                        request.getManufacturerId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Manufacturer not found"));


        // 4. Update fields

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


        // 5. Save

        return repository.save(device);
    }
}
