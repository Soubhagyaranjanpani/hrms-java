package com.hrms.Awards_Recognition.application;

import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SetAwardStatusUseCase {

    private final AwardRepository repository;

    @Transactional
    public void execute(Long id, boolean active) {
        var record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Award record not found with id: " + id));
        record.setIsActive(active);
        repository.save(record);
    }
}