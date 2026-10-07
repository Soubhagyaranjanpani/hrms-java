package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.TransferType;
import com.hrms.master.dto.TransferTypeResponse;
import com.hrms.master.infrastructure.TransferTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTransferTypeUseCase {

    private final TransferTypeRepository transferTypeRepository;

    public ApiResponse<List<TransferTypeResponse>> execute(Integer flag) {

        List<TransferType> list;

        if (flag == 1) {
            list = transferTypeRepository.findByIsActiveTrue();
        } else {
            list = transferTypeRepository.findAll();
        }

        List<TransferTypeResponse> response = list.stream().map(t -> {
            TransferTypeResponse res = new TransferTypeResponse();
            res.setId(t.getId());
            res.setName(t.getName());
            res.setIsActive(t.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}