package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.TransferType;
import com.hrms.master.infrastructure.TransferTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeTransferTypeStatusUseCase {

    private final TransferTypeRepository transferTypeRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        TransferType transferType = transferTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer Type not found"));

        transferType.setIsActive(!transferType.getIsActive());

        transferTypeRepository.save(transferType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Transfer Type Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}