package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.RevisionReason;
import com.hrms.master.infrastructure.RevisionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeRevisionReasonStatusUseCase {

    private final RevisionReasonRepository repository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        RevisionReason reason = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Revision Reason not found"));

        reason.setIsActive(!reason.getIsActive());

        repository.save(reason);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Revision Reason Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}