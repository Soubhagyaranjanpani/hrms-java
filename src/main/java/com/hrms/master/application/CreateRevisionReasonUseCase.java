package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.RevisionReason;
import com.hrms.master.dto.RevisionReasonCreateReq;
import com.hrms.master.infrastructure.RevisionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRevisionReasonUseCase {

    private final RevisionReasonRepository repository;

    public ApiResponse<DefaultResponse> execute(RevisionReasonCreateReq request) {

        if (repository.existsByName(request.getName())) {
            throw new RuntimeException("Revision Reason already exists");
        }

        RevisionReason reason = new RevisionReason();
        reason.setName(request.getName());

        repository.save(reason);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Revision Reason Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}