package com.hrms.promotion.application;

import com.hrms.promotion.dto.PromotionRecordResponse;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeePromotionHistoryUseCase {

    private final PromotionRepository repo;
    private final PromotionMapper     mapper;

    public List<PromotionRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByPromotionDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}