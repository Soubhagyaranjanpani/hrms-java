package com.hrms.transfer.application;

import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.dto.TransferPageResponse;
import com.hrms.transfer.dto.TransferRecordResponse;
import com.hrms.transfer.infrastructure.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTransferListUseCase {

    private final TransferRepository repo;
    private final TransferMapper mapper;

    public TransferPageResponse execute(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TransferRecord> resultPage;

        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsDeletedFalse(pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrDepartmentOrBranchOrReason(search.trim(), pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public TransferPageResponse executeByFlag(Integer flag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Boolean active = (flag == null || flag == 1);

        Page<TransferRecord> resultPage;
        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsActiveAndIsDeletedFalse(active, pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrDepartmentOrBranchOrReasonAndIsActive(search.trim(), active, pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public List<TransferRecordResponse> executeAllByFlag(Integer flag) {
        Boolean active = (flag == null || flag == 1);
        return repo.findByIsActiveAndIsDeletedFalse(active)
                .stream().map(mapper::toResponse).toList();
    }

    private TransferPageResponse toPageResponse(Page<TransferRecord> resultPage, int page, int size) {
        List<TransferRecordResponse> content = resultPage.getContent()
                .stream().map(mapper::toResponse).toList();

        TransferPageResponse response = new TransferPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }
}
