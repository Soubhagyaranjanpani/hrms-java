package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.Manufacturer;
import com.hrms.BiometricDevice.dto.ManufacturerRequest;
import com.hrms.BiometricDevice.infrastructure.ManufacturerInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateManufacturerUseCase {

    private final ManufacturerInfrastructure repository;

    public Manufacturer execute(
            Long id,
            ManufacturerRequest request) {

        Manufacturer manufacturer = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Manufacturer not found"));

        manufacturer.setManufacturerCode(
                request.getManufacturerCode());

        manufacturer.setManufacturerName(
                request.getManufacturerName());

        return repository.save(manufacturer);
    }
}
