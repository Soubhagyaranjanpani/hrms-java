package com.hrms.deputation.application;

import com.hrms.deputation.infrastructure.DeputationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetDeputationStatusUseCase {

    private final DeputationRepository repository;

    public void execute(Long id, boolean active) {
        var record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deputation record not found with id: " + id));
        record.setIsActive(active);
        repository.save(record);
    }
}