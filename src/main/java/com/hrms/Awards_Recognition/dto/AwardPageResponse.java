package com.hrms.Awards_Recognition.dto;


import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AwardPageResponse {
    private List<AwardRecordResponse> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}