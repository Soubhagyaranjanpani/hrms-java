package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.RecruitmentSourcesUseCase;
import com.hrms.Recuirment.domain.RecruitmentSources;
import com.hrms.Recuirment.dto.RecruitmentSourcesReq;
import com.hrms.Recuirment.dto.RecruitmentSourcesResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitment-source")
public class RecruitmentSourcesController {

    private RecruitmentSourcesUseCase recruitmentSourcesUseCase;

    public RecruitmentSourcesController(
            RecruitmentSourcesUseCase recruitmentSourcesUseCase) {

        this.recruitmentSourcesUseCase = recruitmentSourcesUseCase;
    }

    // CREATE
    @PostMapping
    public RecruitmentSources createRecruitmentSource(
            @RequestBody RecruitmentSourcesReq recruitmentSources) {

        return recruitmentSourcesUseCase
                .createRecruitmentSource(recruitmentSources);
    }

    // GET ALL
    @GetMapping
    public List<RecruitmentSourcesResponse> getAllRecruitmentSource() {

        return recruitmentSourcesUseCase.getAllRecruitmentSource();
    }

    // UPDATE
    @PutMapping("/{id}")
    public RecruitmentSources updateRecruitmentSource(
            @PathVariable Long id,
            @RequestBody RecruitmentSources recruitmentSources) {

        return recruitmentSourcesUseCase
                .updateRecruitmentSource(id, recruitmentSources);
    }

    // STATUS CHANGE
    @PatchMapping("/{id}/status")
    public RecruitmentSources changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean status) {

        return recruitmentSourcesUseCase
                .changeStatus(id, status);
    }
}