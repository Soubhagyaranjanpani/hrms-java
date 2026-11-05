package com.hrms.loan_advance.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/** A page of the Approvals list plus the numbers shown on the filter tabs, e.g. {"ALL": 7, "PENDING_FOR_ME": 1}. */
@Getter
@Setter
public class LoanApprovalPageResponse extends LoanPageResponse<LoanAdvanceResponse> {

    private Map<String, Long> tabCounts;

    public static LoanApprovalPageResponse of(LoanPageResponse<LoanAdvanceResponse> page, Map<String, Long> tabCounts) {
        LoanApprovalPageResponse response = new LoanApprovalPageResponse();
        response.setContent(page.getContent());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setCurrentPage(page.getCurrentPage());
        response.setPageSize(page.getPageSize());
        response.tabCounts = tabCounts;
        return response;
    }
}