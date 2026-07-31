package com.hrms.Awards_Recognition.application;


import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Awards_Recognition.dto.AwardPageResponse;
import com.hrms.Awards_Recognition.dto.AwardRecordResponse;
import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAwardListUseCase {

    private final AwardRepository repo;
    private final AwardMapper mapper;

    public AwardPageResponse execute(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AwardRecord> resultPage;

        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsDeletedFalse(pageable);
        } else {
            resultPage = repo.searchByAwardNameOrEmployeeOrType(search.trim(), pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public AwardPageResponse executeByFlag(Integer flag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Boolean active = (flag == null || flag == 1);

        Page<AwardRecord> resultPage;
        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsActiveAndIsDeletedFalse(active, pageable);
        } else {
            resultPage = repo.searchByAwardNameOrEmployeeOrTypeAndIsActive(search.trim(), active, pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public List<AwardRecordResponse> executeAllByFlag(Integer flag) {
        Boolean active = (flag == null || flag == 1);
        return repo.findByIsActiveAndIsDeletedFalse(active)
                .stream().map(mapper::toResponse).toList();
    }

    private AwardPageResponse toPageResponse(Page<AwardRecord> resultPage, int page, int size) {
        List<AwardRecordResponse> content = resultPage.getContent()
                .stream().map(mapper::toResponse).toList();

        AwardPageResponse response = new AwardPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }
}