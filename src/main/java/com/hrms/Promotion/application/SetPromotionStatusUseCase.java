package com.hrms.promotion.application;

import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetPromotionStatusUseCase {

    private final PromotionRepository repository;

    public void execute(Long id, boolean active) {
        PromotionRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion record not found with id: " + id));

        // ✅ Set the correct field: isActive
        record.setIsActive(active);   // active = true → ACTIVE, false → INACTIVE

        repository.save(record);
    }
}