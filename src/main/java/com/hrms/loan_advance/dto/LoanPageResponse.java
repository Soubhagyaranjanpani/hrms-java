package com.hrms.loan_advance.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/** One page of results. Field names match DisciplinaryPageResponse so the frontend can reuse its paging code. */
@Getter
@Setter
public class LoanPageResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;   // 0-based
    private int pageSize;

    public static <T> LoanPageResponse<T> of(Page<T> page) {
        LoanPageResponse<T> r = new LoanPageResponse<>();
        r.setContent(page.getContent());
        r.setTotalElements(page.getTotalElements());
        r.setTotalPages(page.getTotalPages());
        r.setCurrentPage(page.getNumber());
        r.setPageSize(page.getSize());
        return r;
    }
}