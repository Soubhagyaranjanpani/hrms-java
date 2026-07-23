package com.hrms.promotion.application;

import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.dto.PromotionPageResponse;
import com.hrms.promotion.dto.PromotionRecordResponse;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPromotionListUseCase {

    private final PromotionRepository repo;
    private final PromotionMapper mapper;

    // ── Existing ──────────────────────────────
    public PromotionPageResponse execute(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PromotionRecord> resultPage;

        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsDeletedFalse(pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrDesignationOrType(search.trim(), pageable);
        }

        List<PromotionRecordResponse> content = resultPage.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        PromotionPageResponse response = new PromotionPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }

    // ── NEW: with flag (paginated) ──────────
    public PromotionPageResponse executeByFlag(Integer flag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Boolean active = (flag == null || flag == 1) ? true : false;

        Page<PromotionRecord> resultPage;
        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsActiveAndIsDeletedFalse(active, pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrDesignationOrTypeAndIsActive(search.trim(), active, pageable);
        }

        List<PromotionRecordResponse> content = resultPage.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        PromotionPageResponse response = new PromotionPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }

    // ── NEW: with flag (unpaginated) ─────────
    public List<PromotionRecordResponse> executeAllByFlag(Integer flag) {
        Boolean active = (flag == null || flag == 1) ? true : false;
        List<PromotionRecord> records = repo.findByIsActiveAndIsDeletedFalse(active);
        return records.stream()
                .map(mapper::toResponse)
                .toList();
    }
}