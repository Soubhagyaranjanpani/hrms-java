package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.RecruitmentSources;
import com.hrms.Recuirment.dto.RecruitmentSourcesReq;
import com.hrms.Recuirment.dto.RecruitmentSourcesResponse;
import com.hrms.Recuirment.infrastructure.RecruitmentSourceRepository;
import com.hrms.master.domain.SourceCategory;
import com.hrms.master.infrastructure.SourceCategoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;
@Service
public class RecruitmentSourcesUseCase {

    private RecruitmentSourceRepository recruitmentSourceRepository;
    private SourceCategoryRepository sourceCategoryRepository;


    public RecruitmentSourcesUseCase(
            RecruitmentSourceRepository repository,
            SourceCategoryRepository sourceCategoryRepository) {

        this.recruitmentSourceRepository = repository;
        this.sourceCategoryRepository = sourceCategoryRepository;
    }

    // CREATE
    public RecruitmentSources createRecruitmentSource(
            RecruitmentSourcesReq recruitmentSources) {

        RecruitmentSources obj = new RecruitmentSources();

        obj.setSourceCode(recruitmentSources.getSourceCode());
        obj.setSourceName(recruitmentSources.getSourceName());
        obj.setDisplaySequence(recruitmentSources.getDisplaySequence());
        obj.setDescription(recruitmentSources.getDescription());
        obj.setStatus(true);

        SourceCategory category = sourceCategoryRepository
                .findById(recruitmentSources.getCategory())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Source Category not found with id: "
                                        + recruitmentSources.getCategory()
                        ));

        obj.setCategory(category);

        obj.setCreatedAt(LocalDateTime.now());
        obj.setCreatedBy("SYSTEM");

        return recruitmentSourceRepository.save(obj);
    }

    // GET ALL
    public List<RecruitmentSourcesResponse> getAllRecruitmentSource() {

        return recruitmentSourceRepository.findAll().stream().map(this::todto).toList();
    }

    public RecruitmentSourcesResponse todto(RecruitmentSources recruitmentSources) {
        RecruitmentSourcesResponse res=new RecruitmentSourcesResponse();
        res.setId(recruitmentSources.getId());
        res.setSourceCode(recruitmentSources.getSourceCode());
        res.setSourceName(recruitmentSources.getSourceName());
        res.setDescription(recruitmentSources.getDescription());
        res.setDisplaySequence(recruitmentSources.getDisplaySequence());
        res.setCategory(recruitmentSources.getCategory());
        res.setStatus(recruitmentSources.getStatus());
        return res;
    }



    public RecruitmentSources toentity(RecruitmentSources dto){
        RecruitmentSources res = new RecruitmentSources();
        res.setId(dto.getId());
        res.setSourceCode(dto.getSourceCode());
        res.setSourceName(dto.getSourceName());
        res.setDescription(dto.getDescription());
        res.setDisplaySequence(dto.getDisplaySequence());
        res.setStatus(dto.getStatus());
        return res;
    }


    // UPDATE
    public RecruitmentSources updateRecruitmentSource(
            Long id,
            RecruitmentSources recruitmentSources) {

        RecruitmentSources existing =
                recruitmentSourceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruitment Source not found"));

        existing.setSourceName(recruitmentSources.getSourceName());
        existing.setCategory(recruitmentSources.getCategory());
        existing.setDescription(recruitmentSources.getDescription());
        existing.setDisplaySequence(
                recruitmentSources.getDisplaySequence());

        return recruitmentSourceRepository.save(existing);
    }

    // STATUS CHANGE
    public RecruitmentSources changeStatus(
            Long id,
            Boolean status) {

        RecruitmentSources existing =
                recruitmentSourceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruitment Source not found"));

        existing.setStatus(status);

        return recruitmentSourceRepository.save(existing);
    }




}