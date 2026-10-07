package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Designation;
import com.hrms.master.dto.DesignationCreateReq;
import com.hrms.master.infrastructure.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateDesignationUseCase {

    private final DesignationRepository designationRepository;

    public ApiResponse<DefaultResponse> execute(DesignationCreateReq request) {

        if (designationRepository.existsByName(request.getName())) {
            throw new RuntimeException("Designation already exists");
        }

        Designation designation = new Designation();
        designation.setName(request.getName());

        designationRepository.save(designation);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Designation Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}