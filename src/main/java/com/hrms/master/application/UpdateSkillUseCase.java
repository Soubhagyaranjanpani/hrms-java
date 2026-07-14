package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Skill;
import com.hrms.master.dto.SkillUpdateReq;
import com.hrms.master.infrastructure.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateSkillUseCase {

    private final SkillRepository skillRepository;

    public ApiResponse<DefaultResponse> execute(SkillUpdateReq request) {

        Skill skill = skillRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setName(request.getName());

        skillRepository.save(skill);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Skill Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}