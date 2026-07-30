package com.hrms.disciplinary.application;

import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SetDisciplinaryStatusUseCase {

    private final DisciplinaryRepository repository;

    @Transactional
    public void execute(Long id, boolean active) {
        var record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplinary record not found with id: " + id));
        record.setIsActive(active);
        repository.save(record);
    }
}