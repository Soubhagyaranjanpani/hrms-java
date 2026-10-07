package com.hrms.Confirmation.application;

import com.hrms.Confirmation.domain.ConfirmationRecord;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetConfirmationStatusUseCase {

    private final ConfirmationRepository repository;

    public void execute(Long id, boolean active) {
        ConfirmationRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Confirmation record not found with id: " + id));

        record.setIsActive(active);
        repository.save(record);
    }
}
