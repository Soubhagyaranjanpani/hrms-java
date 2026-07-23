package com.hrms.promotion.application;

import com.hrms.promotion.dto.PromotionStatsResponse;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPromotionStatsUseCase {

    private final PromotionRepository repo;

    public PromotionStatsResponse execute(String year) {
        Object[] agg = repo.aggregateForYear(year);
        PromotionStatsResponse s = new PromotionStatsResponse();

        if (agg == null || agg[0] == null) {
            s.setTotalCount(0); s.setActiveCount(0); s.setInactiveCount(0);
            s.setAvgIncrementAmount(0.0); s.setAvgIncrementPercent(0.0); s.setTotalIncrementAmount(0.0);
            return s;
        }

        s.setTotalCount(toInt(agg[0]));
        s.setActiveCount(toInt(agg[1]));
        s.setInactiveCount(toInt(agg[2]));
        s.setAvgIncrementAmount(toDouble(agg[3]));
        s.setAvgIncrementPercent(toDouble(agg[4]));
        s.setTotalIncrementAmount(toDouble(agg[5]));

        return s;
    }

    private double toDouble(Object o) { return o == null ? 0 : ((Number) o).doubleValue(); }
    private int    toInt(Object o)    { return o == null ? 0 : ((Number) o).intValue(); }
}