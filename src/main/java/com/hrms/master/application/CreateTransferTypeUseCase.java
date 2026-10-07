package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.TransferType;
import com.hrms.master.dto.TransferTypeCreateReq;
import com.hrms.master.infrastructure.TransferTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTransferTypeUseCase {

    private final TransferTypeRepository transferTypeRepository;

    public ApiResponse<DefaultResponse> execute(TransferTypeCreateReq request) {

        if (transferTypeRepository.existsByName(request.getName())) {
            throw new RuntimeException("Transfer Type already exists");
        }

        TransferType transferType = new TransferType();
        transferType.setName(request.getName());

        transferTypeRepository.save(transferType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Transfer Type Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}