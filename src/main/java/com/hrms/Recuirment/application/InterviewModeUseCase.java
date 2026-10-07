package com.hrms.Recuirment.application;


import com.hrms.Recuirment.domain.InterviewMode;
import com.hrms.Recuirment.dto.InterviewModeReq;
import com.hrms.Recuirment.dto.InterviewModeResponse;
import com.hrms.Recuirment.infrastructure.InterviewModeRepository;
import com.hrms.common.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewModeUseCase {
    @Autowired
    private InterviewModeRepository interviewModeRepository;
    @Autowired
    private CurrentUser currentUser;


    public String createInterviewMode(InterviewModeReq createReq){

        InterviewMode interviewMode = new InterviewMode();
        interviewMode.setName(createReq.getName());
        interviewMode.setStatus(createReq.getStatus());
        interviewMode.setCreatedAt(LocalDateTime.now());
        interviewMode.setCreatedBy(currentUser.getEmployee().getFirstName());

        interviewModeRepository.save(interviewMode);

        return "save a successfully";

    }

    public List<InterviewModeResponse> getAllInterviewMode(){
        List<InterviewModeResponse>InterviewList= interviewModeRepository.findAll().stream().map(this::todto).toList();
        return InterviewList;
    }

    private InterviewModeResponse todto(InterviewMode interviewMode) {
        InterviewModeResponse res= new InterviewModeResponse();
        res.setId(interviewMode.getId());
        res.setName(interviewMode.getName());
        res.setStatus(interviewMode.getStatus());
        res.setCreatedAt(interviewMode.getCreatedAt());
        return res;
    }


    private InterviewMode toentity(InterviewModeResponse interviewMode){
        InterviewMode res=new InterviewMode();
        res.setId(interviewMode.getId());
        res.setName(interviewMode.getName());
        res.setStatus(interviewMode.getStatus());
        return res;


    }





    public InterviewModeResponse updateById(Long id,InterviewModeReq updateData){
        InterviewMode existingData= interviewModeRepository.findById(id).get();

        if(existingData!=null){
            existingData.setName(updateData.getName());
            existingData.setStatus(updateData.getStatus());
            InterviewMode res = interviewModeRepository.save(existingData);
            return todto(res);
        }else {
            return  null;
        }
    }


    public String updateStatusById(Long id) {
        Optional<InterviewMode>optionalData= interviewModeRepository.findById(id);
        if(optionalData.isPresent()){
            InterviewMode existingData= optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            interviewModeRepository.save(existingData);

            return ("status update successfully");
        }else {
            return "country not found !";
        }

    }

}
