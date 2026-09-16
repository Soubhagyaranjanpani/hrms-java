package com.hrms.BiometricDevice.infrastructure;

import com.hrms.BiometricDevice.domain.BiometricDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiometricDeviceInfrastructure
        extends JpaRepository<BiometricDevice, Long> {

    boolean existsByDeviceCode(String deviceCode);

    boolean existsBySerialNumber(String serialNumber);

    List<BiometricDevice> findByIsActiveTrue();
}