package com.hrms.promotion.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PromotionStatsResponse {
    private Integer totalCount;
    private Integer activeCount;
    private Integer inactiveCount;
    private Double  avgIncrementAmount;
    private Double  avgIncrementPercent;
    private Double  totalIncrementAmount;
}