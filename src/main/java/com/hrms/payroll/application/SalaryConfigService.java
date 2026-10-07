package com.hrms.payroll.application;

import com.hrms.payroll.domain.SalaryConfiguration;
import com.hrms.payroll.infrastructure.SalaryConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryConfigService {

    private final SalaryConfigurationRepository configRepo;

    public Map<String, Double> getAllConfigValues() {
        List<SalaryConfiguration> all = configRepo.findAll();
        Map<String, Double> map = new LinkedHashMap<>();
        for (SalaryConfiguration c : all) {
            if (c.getIsActive() != null && c.getIsActive()) {
                map.put(c.getConfigKey(), c.getConfigValue());
            }
        }
        return map;
    }

    public Double getValue(String key) {
        return configRepo.findByConfigKey(key)
                .filter(c -> c.getIsActive() != null && c.getIsActive())
                .map(SalaryConfiguration::getConfigValue)
                .orElse(null);
    }

    public List<SalaryConfiguration> getAllConfigs() {
        return configRepo.findAll();
    }

    public SalaryConfiguration getById(Long id) {
        return configRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration not found: " + id));
    }

    @Transactional
    public SalaryConfiguration create(SalaryConfiguration config) {
        if (config.getIsActive() == null) {
            config.setIsActive(true);
        }
        return configRepo.save(config);
    }

    @Transactional
    public SalaryConfiguration update(Long id, Double newValue) {
        SalaryConfiguration config = getById(id);
        config.setConfigValue(newValue);
        return configRepo.save(config);
    }

    @Transactional
    public SalaryConfiguration updateByKey(String key, Double newValue) {
        SalaryConfiguration config = configRepo.findByConfigKey(key)
                .orElseThrow(() -> new RuntimeException("Configuration not found: " + key));
        config.setConfigValue(newValue);
        return configRepo.save(config);
    }

    @Transactional
    public SalaryConfiguration updateFull(Long id, SalaryConfiguration updated) {
        SalaryConfiguration existing = getById(id);
        existing.setConfigKey(updated.getConfigKey());
        existing.setConfigName(updated.getConfigName());
        existing.setConfigValue(updated.getConfigValue());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setIsActive(updated.getIsActive() != null ? updated.getIsActive() : existing.getIsActive());
        return configRepo.save(existing);
    }

    @Transactional
    public void toggleStatus(Long id) {
        SalaryConfiguration config = getById(id);
        config.setIsActive(config.getIsActive() == null || !config.getIsActive());
        configRepo.save(config);
    }

    @Transactional
    public List<SalaryConfiguration> bulkUpdate(Map<String, Double> updates) {
        return updates.entrySet().stream()
                .map(entry -> updateByKey(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}