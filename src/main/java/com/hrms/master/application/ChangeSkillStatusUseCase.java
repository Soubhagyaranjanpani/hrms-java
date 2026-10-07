package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Skill;
import com.hrms.master.infrastructure.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeSkillStatusUseCase {

    private final SkillRepository skillRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setIsActive(!skill.getIsActive());

        skillRepository.save(skill);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Skill Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}