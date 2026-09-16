package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.Manufacturer;
import com.hrms.BiometricDevice.dto.ManufacturerResponse;
import com.hrms.BiometricDevice.infrastructure.ManufacturerInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetManufacturerByIdUseCase {

    private final ManufacturerInfrastructure repository;

    public ManufacturerResponse execute(Long id) {

        Manufacturer manufacturer = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Manufacturer not found"));

        return new ManufacturerResponse(
                manufacturer.getId(),
                manufacturer.getManufacturerCode(),
                manufacturer.getManufacturerName(),
                manufacturer.getIsActive()
        );
    }
}
