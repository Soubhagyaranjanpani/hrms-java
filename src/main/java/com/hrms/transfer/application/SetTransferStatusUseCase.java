package com.hrms.transfer.application;

import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.infrastructure.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetTransferStatusUseCase {

    private final TransferRepository repository;

    public void execute(Long id, boolean active) {
        TransferRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer record not found with id: " + id));

        record.setIsActive(active);
        repository.save(record);
    }
}
