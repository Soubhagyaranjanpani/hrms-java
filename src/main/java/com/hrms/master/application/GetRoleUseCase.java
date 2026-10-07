package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Role;
import com.hrms.master.dto.RoleResponse;
import com.hrms.master.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRoleUseCase {

    private final RoleRepository roleRepository;

    public ApiResponse<List<RoleResponse>> execute(Integer flag) {

        List<Role> roles;

        if (flag == 1) {
            roles = roleRepository.findByIsActiveTrue();
        } else {
            roles = roleRepository.findAll();
        }

        List<RoleResponse> response = roles.stream().map(r -> {
            RoleResponse res = new RoleResponse();
            res.setId(r.getId());
            res.setName(r.getName());
            res.setIsActive(r.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}