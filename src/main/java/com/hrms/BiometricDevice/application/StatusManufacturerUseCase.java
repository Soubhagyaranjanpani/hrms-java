package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.Manufacturer;
import com.hrms.BiometricDevice.infrastructure.ManufacturerInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusManufacturerUseCase {

    private final ManufacturerInfrastructure repository;

    public Manufacturer execute(Long id, Boolean status) {

        Manufacturer manufacturer = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Manufacturer not found"));

        manufacturer.setIsActive(status);

        return repository.save(manufacturer);
    }
}
