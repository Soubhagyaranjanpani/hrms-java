package com.hrms.promotion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PromotionPageResponse {
    private List<PromotionRecordResponse> content;
    private long totalElements;
    private int  totalPages;
    private int  currentPage;
    private int  pageSize;
}