package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.RevisionReason;
import com.hrms.master.dto.RevisionReasonUpdateReq;
import com.hrms.master.infrastructure.RevisionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateRevisionReasonUseCase {

    private final RevisionReasonRepository repository;

    public ApiResponse<DefaultResponse> execute(RevisionReasonUpdateReq request) {

        RevisionReason reason = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Revision Reason not found"));

        reason.setName(request.getName());

        repository.save(reason);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Revision Reason Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}