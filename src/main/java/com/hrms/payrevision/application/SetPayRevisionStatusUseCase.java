package com.hrms.payrevision.application;

import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetPayRevisionStatusUseCase {

    private final PayRevisionRepository repository;

    public void execute(Long id, boolean active) {
        PayRevisionRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pay revision record not found with id: " + id));

        record.setIsActive(active);
        repository.save(record);
    }
}
