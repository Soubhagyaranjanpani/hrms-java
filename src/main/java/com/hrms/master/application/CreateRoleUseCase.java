package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Role;
import com.hrms.master.dto.RoleCreateReq;
import com.hrms.master.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {

    private final RoleRepository roleRepository;

    public ApiResponse<DefaultResponse> execute(RoleCreateReq request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role already exists");
        }

        Role role = new Role();
        role.setName(request.getName());

        roleRepository.save(role);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Role Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}