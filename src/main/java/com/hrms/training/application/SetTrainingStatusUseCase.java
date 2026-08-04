package com.hrms.training.application;

import com.hrms.training.domain.TrainingRecord;
import com.hrms.training.infrastructure.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetTrainingStatusUseCase {

    private final TrainingRepository repository;

    public void execute(Long id, boolean active) {
        TrainingRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training record not found with id: " + id));

        record.setIsActive(active);
        repository.save(record);
    }
}
