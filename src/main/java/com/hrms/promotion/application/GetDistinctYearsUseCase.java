package com.hrms.promotion.application;

import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDistinctYearsUseCase {

    private final PromotionRepository repo;

    public List<String> execute() {
        return repo.findDistinctYears();
    }
}