package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Designation;
import com.hrms.master.dto.DesignationUpdateReq;
import com.hrms.master.infrastructure.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDesignationUseCase {

    private final DesignationRepository designationRepository;

    public ApiResponse<DefaultResponse> execute(DesignationUpdateReq request) {

        Designation designation = designationRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        designation.setName(request.getName());

        designationRepository.save(designation);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Designation Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
