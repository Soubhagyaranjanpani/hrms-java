package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Role;
import com.hrms.master.dto.RoleUpdateReq;
import com.hrms.master.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase {

    private final RoleRepository roleRepository;

    public ApiResponse<DefaultResponse> execute(RoleUpdateReq request) {

        Role role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(request.getName());

        roleRepository.save(role);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Role Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}