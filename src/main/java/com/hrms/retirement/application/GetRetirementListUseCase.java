package com.hrms.retirement.application;

import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.dto.RetirementPageResponse;
import com.hrms.retirement.dto.RetirementRecordResponse;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRetirementListUseCase {

    private final RetirementRepository repo;
    private final RetirementMapper mapper;

    public RetirementPageResponse execute(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RetirementRecord> resultPage;

        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsDeletedFalse(pageable);
        } else {
            resultPage = repo.searchByEmployeeOrTypeOrPensionOrOrder(search.trim(), pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public RetirementPageResponse executeByFlag(Integer flag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Boolean active = (flag == null || flag == 1);

        Page<RetirementRecord> resultPage;
        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsActiveAndIsDeletedFalse(active, pageable);
        } else {
            resultPage = repo.searchByEmployeeOrTypeOrPensionOrOrderAndIsActive(search.trim(), active, pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public List<RetirementRecordResponse> executeAllByFlag(Integer flag) {
        Boolean active = (flag == null || flag == 1);
        return repo.findByIsActiveAndIsDeletedFalse(active)
                .stream().map(mapper::toResponse).toList();
    }

    private RetirementPageResponse toPageResponse(Page<RetirementRecord> resultPage, int page, int size) {
        List<RetirementRecordResponse> content = resultPage.getContent()
                .stream().map(mapper::toResponse).toList();

        RetirementPageResponse response = new RetirementPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }
}
