package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.RevisionReason;
import com.hrms.master.dto.RevisionReasonResponse;
import com.hrms.master.infrastructure.RevisionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRevisionReasonUseCase {

    private final RevisionReasonRepository repository;

    public ApiResponse<List<RevisionReasonResponse>> execute(Integer flag) {

        List<RevisionReason> list;

        if (flag == 1) {
            list = repository.findByIsActiveTrue();
        } else {
            list = repository.findAll();
        }

        List<RevisionReasonResponse> response = list.stream().map(r -> {
            RevisionReasonResponse res = new RevisionReasonResponse();
            res.setId(r.getId());
            res.setName(r.getName());
            res.setIsActive(r.getIsActive());
            res.setCreatedAt(r.getCreatedAt());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}