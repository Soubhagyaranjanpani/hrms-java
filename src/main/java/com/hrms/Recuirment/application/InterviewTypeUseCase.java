package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.InterviewMode;
import com.hrms.Recuirment.domain.InterviewType;
import com.hrms.Recuirment.dto.InterviewTypeReq;
import com.hrms.Recuirment.dto.InterviewTypeResponse;
import com.hrms.Recuirment.infrastructure.InterviewModeRepository;
import com.hrms.Recuirment.infrastructure.InterviewTypeRepository;
import com.hrms.common.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewTypeUseCase {
    @Autowired
    private InterviewTypeRepository interviewTypeRepository;
    @Autowired
    private InterviewModeRepository interviewModeRepository;
    @Autowired
    private CurrentUser currentUser;

    public String saveInterviewType(InterviewTypeReq createReq) {
        InterviewMode interviewMode = interviewModeRepository.findById(createReq.getInterviewModeId())
                            .orElseThrow(() -> new RuntimeException("interviewMode not found"));
        InterviewType createObj = new InterviewType();
        createObj.setInterviewTypeCode(createReq.getInterviewTypeCode());
        createObj.setInterviewTypeName(createReq.getInterviewTypeName());
        createObj.setDescription(createReq.getDescription());
        createObj.setStatus("y");
        createObj.setInterviewMode(interviewMode);
        createObj.setCreatedAt(LocalDateTime.now());
        createObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        InterviewType save = interviewTypeRepository.save(createObj);
        return "InterviewType save successfully";
    }

    public List<InterviewTypeResponse> getAllInterviewType() {
        List<InterviewTypeResponse> interviewTypeList = interviewTypeRepository.findAll().stream().map(this::todto).toList();
        return interviewTypeList;
    }

    public List<InterviewTypeResponse> getInterviewTypeByInterviewMode(Long id) {
        List<InterviewTypeResponse> interviewType = interviewTypeRepository.findByInterviewModeId(id).stream().map(this::todto).toList();
        return interviewType;
    }

    public InterviewTypeResponse updateInterviewTypeById(Long id, InterviewTypeReq updatedData) {
        InterviewType existingData = interviewTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("interviewType not found" + id));
        if (existingData != null) {
            //update basic fields
            existingData.setInterviewTypeCode(updatedData.getInterviewTypeCode());
            existingData.setInterviewTypeName(updatedData.getInterviewTypeName());
            existingData.setDescription(updatedData.getDescription());
            existingData.setStatus(updatedData.getStatus());

        //update interviewMode using interviewModeId
        InterviewMode interviewMode = interviewModeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("interviewMode not found"
                                + updatedData.getInterviewModeId()));
        if (interviewMode != null) {
            existingData.setInterviewMode(interviewMode);
        }
        //save the entity
        InterviewType savedData = interviewTypeRepository.save(existingData);
        return todto(savedData);
        }else {
            return null;
        }
}
    public String updateInterviewTypeStatus(Long id){
        Optional<InterviewType>optionalData=interviewTypeRepository.findById(id);
        if(optionalData.isPresent()){
            InterviewType existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            interviewTypeRepository.save(existingData);
            return "update interviewType successfully";
        }else{
            return "interviewType not found";
        }
    }

    private InterviewTypeResponse todto(InterviewType interviewType) {
        InterviewTypeResponse res=new InterviewTypeResponse();
        res.setId(interviewType.getId());
        res.setInterviewTypeCode(interviewType.getInterviewTypeCode());
        res.setInterviewTypeName(interviewType.getInterviewTypeName());
        res.setDescription(interviewType.getDescription());
        res.setInterviewModeId(res.getInterviewModeId());
        res.setStatus(interviewType.getStatus());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        return res;
    }
    private InterviewType toentity(InterviewTypeResponse dto){
        InterviewType response=new InterviewType();
        response.setId(dto.getId());
        response.setInterviewTypeCode(dto.getInterviewTypeCode());
        response.setInterviewTypeName(dto.getInterviewTypeName());
        response.setDescription(dto.getDescription());
        response.setStatus(dto.getStatus());

        InterviewMode interviewMode=new InterviewMode();
        interviewMode.setId(dto.getInterviewModeId());
        response.setInterviewMode(interviewMode);
        return response;
    }
}
