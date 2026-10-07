package com.hrms.Confirmation.application;
import com.hrms.Confirmation.domain.ConfirmationRecord;
import com.hrms.Confirmation.dto.ConfirmationPageResponse;
import com.hrms.Confirmation.dto.ConfirmationRecordResponse;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetConfirmationListUseCase {

    private final ConfirmationRepository repo;
    private final ConfirmationMapper mapper;

    public ConfirmationPageResponse execute(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ConfirmationRecord> resultPage;

        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsDeletedFalse(pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrConfirmedByOrRemarks(search.trim(), pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public ConfirmationPageResponse executeByFlag(Integer flag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Boolean active = (flag == null || flag == 1);

        Page<ConfirmationRecord> resultPage;
        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsActiveAndIsDeletedFalse(active, pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrConfirmedByOrRemarksAndIsActive(search.trim(), active, pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public List<ConfirmationRecordResponse> executeAllByFlag(Integer flag) {
        Boolean active = (flag == null || flag == 1);
        return repo.findByIsActiveAndIsDeletedFalse(active)
                .stream().map(mapper::toResponse).toList();
    }

    private ConfirmationPageResponse toPageResponse(Page<ConfirmationRecord> resultPage, int page, int size) {
        List<ConfirmationRecordResponse> content = resultPage.getContent()
                .stream().map(mapper::toResponse).toList();

        ConfirmationPageResponse response = new ConfirmationPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }
}
