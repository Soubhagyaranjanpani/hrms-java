package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.Manufacturer;
import com.hrms.BiometricDevice.dto.ManufacturerRequest;
import com.hrms.BiometricDevice.infrastructure.ManufacturerInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateManufacturerUseCase {

    private final ManufacturerInfrastructure repository;

    public Manufacturer execute(ManufacturerRequest request) {

        if (repository.existsByManufacturerCode(
                request.getManufacturerCode())) {

            throw new RuntimeException(
                    "Manufacturer code already exists");
        }

        if (repository.existsByManufacturerName(
                request.getManufacturerName())) {

            throw new RuntimeException(
                    "Manufacturer name already exists");
        }

        Manufacturer manufacturer = new Manufacturer();

        manufacturer.setManufacturerCode(
                request.getManufacturerCode());

        manufacturer.setManufacturerName(
                request.getManufacturerName());

        manufacturer.setIsActive(true);

        return repository.save(manufacturer);
    }
}
