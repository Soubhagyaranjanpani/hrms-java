package com.hrms.disciplinary.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DisciplinaryPageResponse {
    private List<DisciplinaryRecordResponse> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}