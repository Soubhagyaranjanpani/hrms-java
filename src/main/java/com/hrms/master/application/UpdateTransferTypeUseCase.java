package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.TransferType;
import com.hrms.master.dto.TransferTypeUpdateReq;
import com.hrms.master.infrastructure.TransferTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTransferTypeUseCase {

    private final TransferTypeRepository transferTypeRepository;

    public ApiResponse<DefaultResponse> execute(TransferTypeUpdateReq request) {

        TransferType transferType = transferTypeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Transfer Type not found"));

        transferType.setName(request.getName());

        transferTypeRepository.save(transferType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Transfer Type Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}