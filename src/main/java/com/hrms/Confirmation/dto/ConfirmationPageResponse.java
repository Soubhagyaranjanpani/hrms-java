package com.hrms.Confirmation.dto;

import com.hrms.Confirmation.dto.ConfirmationRecordResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfirmationPageResponse {
    private List<ConfirmationRecordResponse> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
