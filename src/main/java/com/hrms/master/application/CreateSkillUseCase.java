package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Skill;
import com.hrms.master.dto.SkillCreateReq;
import com.hrms.master.infrastructure.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateSkillUseCase {

    private final SkillRepository skillRepository;

    public ApiResponse<DefaultResponse> execute(SkillCreateReq request) {

        if (skillRepository.existsByName(request.getName())) {
            throw new RuntimeException("Skill already exists");
        }

        Skill skill = new Skill();
        skill.setName(request.getName());

        skillRepository.save(skill);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Skill Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}