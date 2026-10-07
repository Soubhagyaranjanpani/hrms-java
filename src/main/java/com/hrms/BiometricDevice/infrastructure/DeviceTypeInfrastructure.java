package com.hrms.BiometricDevice.infrastructure;

import com.hrms.BiometricDevice.domain.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceTypeInfrastructure
        extends JpaRepository<DeviceType, Long> {

    boolean existsByDeviceTypeCode(String deviceTypeCode);

    boolean existsByDeviceTypeName(String deviceTypeName);

    List<DeviceType> findByIsActiveTrue();
}
