package com.hrms.BiometricDevice.application;

import com.hrms.BiometricDevice.domain.Manufacturer;
import com.hrms.BiometricDevice.dto.ManufacturerResponse;
import com.hrms.BiometricDevice.infrastructure.ManufacturerInfrastructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllManufacturerUseCase {

    private final ManufacturerInfrastructure repository;

    public List<ManufacturerResponse> execute(Integer flag) {

        List<Manufacturer> manufacturers;

        if (flag != null && flag == 1) {
            manufacturers = repository.findByIsActiveTrue();
        } else {
            manufacturers = repository.findAll();
        }

        return manufacturers.stream()
                .map(manufacturer -> new ManufacturerResponse(
                        manufacturer.getId(),
                        manufacturer.getManufacturerCode(),
                        manufacturer.getManufacturerName(),
                        manufacturer.getIsActive()
                ))
                .collect(Collectors.toList());
    }
}
