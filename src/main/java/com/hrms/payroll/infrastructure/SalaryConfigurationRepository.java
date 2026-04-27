package com.hrms.payroll.infrastructure;

import com.hrms.payroll.domain.SalaryConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SalaryConfigurationRepository extends JpaRepository<SalaryConfiguration, Long> {
    Optional<SalaryConfiguration> findByConfigKeyAndIsActiveTrue(String configKey);
    Optional<SalaryConfiguration> findByConfigKey(String configKey);
}