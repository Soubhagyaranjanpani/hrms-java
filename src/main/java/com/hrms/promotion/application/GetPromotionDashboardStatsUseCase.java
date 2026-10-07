package com.hrms.promotion.application;

import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.dto.PromotionDashboardStats;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPromotionDashboardStatsUseCase {

    private final PromotionRepository repo;

    public PromotionDashboardStats execute(String year) {
        if (year == null || year.isEmpty()) {
            year = String.valueOf(LocalDate.now().getYear());
        }

        PromotionDashboardStats s = new PromotionDashboardStats();
        List<PromotionRecord> records = repo.findByPromotionYearAndIsDeletedFalse(year);

        if (records.isEmpty()) {
            setDefaults(s);
            s.setAiSummary("No promotions recorded for " + year + " yet.");
            s.setTrend(new ArrayList<>());
            s.setDeptBreakdown(new ArrayList<>());
            return s;
        }

        long active   = records.stream().filter(r -> Boolean.TRUE.equals(r.getIsActive())).count();
        long inactive = records.stream().filter(r -> Boolean.FALSE.equals(r.getIsActive())).count();

        double avgAmt = records.stream().mapToDouble(r -> r.getIncrementAmount() != null ? r.getIncrementAmount() : 0).average().orElse(0);
        double avgPct = records.stream().mapToDouble(r -> r.getIncrementPercent() != null ? r.getIncrementPercent() : 0).average().orElse(0);

        s.setTotalPromotions(records.size());
        s.setActiveCount((int) active);
        s.setInactiveCount((int) inactive);
        s.setAvgIncrementAmount(avgAmt);
        s.setAvgIncrementPercent(avgPct);

        s.setDeptBreakdown(buildDeptBreakdown(records));
        s.setTrend(new ArrayList<>()); // optional: fill using repo query across years
        s.setAiSummary(String.format("%s: %d promotions, avg increment %.1f%%.", year, records.size(), avgPct));

        return s;
    }

    private List<PromotionDashboardStats.DeptBreakdown> buildDeptBreakdown(List<PromotionRecord> records) {
        Map<String, List<PromotionRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> (r.getNewDepartment() != null ? r.getNewDepartment() : "Unassigned").toString()));

        List<PromotionDashboardStats.DeptBreakdown> list = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            PromotionDashboardStats.DeptBreakdown db = new PromotionDashboardStats.DeptBreakdown();
            db.setDepartment(entry.getKey());
            db.setCount(entry.getValue().size());
            db.setAvgIncrementAmount(entry.getValue().stream()
                    .mapToDouble(r -> r.getIncrementAmount() != null ? r.getIncrementAmount() : 0)
                    .average().orElse(0));
            list.add(db);
        }
        list.sort((a, b) -> b.getCount() - a.getCount());
        return list;
    }

    private void setDefaults(PromotionDashboardStats s) {
        s.setTotalPromotions(0); s.setActiveCount(0); s.setInactiveCount(0);
        s.setAvgIncrementAmount(0.0); s.setAvgIncrementPercent(0.0);
    }
}