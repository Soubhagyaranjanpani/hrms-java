package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Role;
import com.hrms.master.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeRoleStatusUseCase {

    private final RoleRepository roleRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setIsActive(!role.getIsActive());

        roleRepository.save(role);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Role Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}