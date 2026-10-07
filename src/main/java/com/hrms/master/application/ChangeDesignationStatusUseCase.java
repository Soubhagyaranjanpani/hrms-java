package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Designation;
import com.hrms.master.infrastructure.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeDesignationStatusUseCase {

    private final DesignationRepository designationRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        designation.setIsActive(!designation.getIsActive());

        designationRepository.save(designation);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Designation Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}