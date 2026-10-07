package com.hrms.retirement.application;

import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetRetirementStatusUseCase {

    private final RetirementRepository repository;

    public void execute(Long id, boolean active) {
        RetirementRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Retirement record not found with id: " + id));

        record.setIsActive(active);
        repository.save(record);
    }
}
