package com.hrms.payrevision.application;

import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.dto.PayRevisionPageResponse;
import com.hrms.payrevision.dto.PayRevisionRecordResponse;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPayRevisionListUseCase {

    private final PayRevisionRepository repo;
    private final PayRevisionMapper mapper;

    public PayRevisionPageResponse execute(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PayRevisionRecord> resultPage;

        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsDeletedFalse(pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrReasonOrRemarks(search.trim(), pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public PayRevisionPageResponse executeByFlag(Integer flag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Boolean active = (flag == null || flag == 1);

        Page<PayRevisionRecord> resultPage;
        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsActiveAndIsDeletedFalse(active, pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrReasonOrRemarksAndIsActive(search.trim(), active, pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public List<PayRevisionRecordResponse> executeAllByFlag(Integer flag) {
        Boolean active = (flag == null || flag == 1);
        return repo.findByIsActiveAndIsDeletedFalse(active)
                .stream().map(mapper::toResponse).toList();
    }

    private PayRevisionPageResponse toPageResponse(Page<PayRevisionRecord> resultPage, int page, int size) {
        List<PayRevisionRecordResponse> content = resultPage.getContent()
                .stream().map(mapper::toResponse).toList();

        PayRevisionPageResponse response = new PayRevisionPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }
}
