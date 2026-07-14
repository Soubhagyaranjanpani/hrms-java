package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Skill;
import com.hrms.master.dto.SkillResponse;
import com.hrms.master.infrastructure.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetSkillUseCase {

    private final SkillRepository skillRepository;

    public ApiResponse<List<SkillResponse>> execute(Integer flag) {

        List<Skill> list;

        if (flag == 1) {
            list = skillRepository.findByIsActiveTrue();
        } else {
            list = skillRepository.findAll();
        }

        List<SkillResponse> response = list.stream().map(s -> {
            SkillResponse res = new SkillResponse();
            res.setId(s.getId());
            res.setName(s.getName());
            res.setIsActive(s.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}