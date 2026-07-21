package com.hrms.promotion.infrastructure;

import com.hrms.promotion.domain.PromotionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionHistoryRepository extends JpaRepository<PromotionHistory, Long> {
    boolean existsByPromotionOrderNumber(String promotionOrderNumber);
}