package com.hrms.BiometricDevice.infrastructure;

import com.hrms.BiometricDevice.domain.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturerInfrastructure
        extends JpaRepository<Manufacturer, Long> {

    boolean existsByManufacturerCode(String manufacturerCode);

    boolean existsByManufacturerName(String manufacturerName);

    List<Manufacturer> findByIsActiveTrue();
}
