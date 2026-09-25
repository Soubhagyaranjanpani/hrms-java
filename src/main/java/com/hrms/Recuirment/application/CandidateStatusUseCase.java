package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.CandidateStatus;
import com.hrms.Recuirment.domain.StatusCategory;
import com.hrms.Recuirment.dto.CandidateStatusReq;
import com.hrms.Recuirment.dto.CandidateStatusResponse;
import com.hrms.Recuirment.infrastructure.CandidateStatusRepository;
import com.hrms.Recuirment.infrastructure.StatusCategoryRepository;
import com.hrms.common.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateStatusUseCase {
    @Autowired
    private final CandidateStatusRepository candidateStatusRepository;
    @Autowired
    private final StatusCategoryRepository statusCategoryRepository;
    @Autowired
    private final CurrentUser currentUser;
    public String saveCandidateStatus(CandidateStatusReq statusReq) {
        StatusCategory statusCategory=statusCategoryRepository.findById(statusReq.getStatusCategoryId())
                                       .orElseThrow(()->new RuntimeException("statusCategory not found"));
        CandidateStatus createObj=new CandidateStatus();
        createObj.setStatusCode(statusReq.getStatusCode());
        createObj.setStatusName(statusReq.getStatusName());
        createObj.setDisplayOrder(statusReq.getDisplayOrder());
        createObj.setFinalStatus(statusReq.getFinalStatus());
        createObj.setStatusColor(statusReq.getStatusColor());
        createObj.setDescription(statusReq.getDescription());
        createObj.setStatus("y");
        createObj.setStatusCategory(statusCategory);
        createObj.setCreatedAt(LocalDateTime.now());
        createObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        CandidateStatus save=candidateStatusRepository.save(createObj);
        return "Candidate Status save successfully";
    }

    public List<CandidateStatusResponse> getAllCandidateStatus() {
        List<CandidateStatusResponse>candidateStatusList=candidateStatusRepository.findAll().stream().map(this::todto).toList();
        return candidateStatusList;
    }
    public CandidateStatusResponse updateCandidateStatusById(Long id, CandidateStatusReq updatedData) {
        CandidateStatus existingData=candidateStatusRepository.findById(id)
                                       .orElseThrow(()->new RuntimeException("candidateStatus not found"+id));
        if(existingData!=null){
            //update Basic fields;
            existingData.setStatusCode(updatedData.getStatusCode());
            existingData.setStatusName(updatedData.getStatusName());
            existingData.setDisplayOrder(updatedData.getDisplayOrder());
            existingData.setFinalStatus(updatedData.getFinalStatus());
            existingData.setStatusColor(updatedData.getStatusColor());
            existingData.setDescription(updatedData.getDescription());
            //get statusCategory using statusCategoryId
            StatusCategory statusCategory=statusCategoryRepository.findById(updatedData.getStatusCategoryId())
                                           .orElseThrow(()->new RuntimeException("statusCategory not found"+id));
            if(statusCategory!=null){
                existingData.setStatusCategory(statusCategory);
            }
            //save updated entity
            CandidateStatus savedData=candidateStatusRepository.save(existingData);
            return todto(savedData);
        }else {
            return null;
        }
    }
    public String updateStatusOfCandidateStatus(Long id) {
        Optional<CandidateStatus>optionalData=candidateStatusRepository.findById(id);
        if(optionalData.isPresent()){
            CandidateStatus existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            candidateStatusRepository.save(existingData);
            return "update candidateStatus successfully";
        }else{
            return "CandidateStatus not found";
        }
    }

    private CandidateStatusResponse todto(CandidateStatus candidateStatus) {
        CandidateStatusResponse res=new CandidateStatusResponse();
        res.setId(candidateStatus.getId());
        res.setStatusCode(candidateStatus.getStatusCode());
        res.setStatusName(candidateStatus.getStatusName());
        res.setFinalStatus(candidateStatus.getFinalStatus());
        res.setDisplayOrder(candidateStatus.getDisplayOrder());
        res.setStatusColor(candidateStatus.getStatusColor());
        res.setDescription(candidateStatus.getDescription());
        res.setStatus(candidateStatus.getStatus());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        res.setStatusCategoryId(candidateStatus.getStatusCategory().getId());
        return res;
    }
    private CandidateStatus toentity(CandidateStatusResponse dto){
        CandidateStatus res=new CandidateStatus();
        res.setId(dto.getId());
        res.setStatusCode(dto.getStatusCode());
        res.setStatusName(dto.getStatusName());
        res.setFinalStatus(dto.getFinalStatus());
        res.setStatusColor(dto.getStatusColor());
        res.setDisplayOrder(dto.getDisplayOrder());
        res.setDescription(dto.getDescription());
        res.setStatus(dto.getStatus());

        StatusCategory statusCategory=new StatusCategory();
        statusCategory.setId(dto.getStatusCategoryId());
        res.setStatusCategory(statusCategory);
        return res;
    }
}
