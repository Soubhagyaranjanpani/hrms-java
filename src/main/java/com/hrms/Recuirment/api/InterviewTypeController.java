package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.InterviewTypeUseCase;
import com.hrms.Recuirment.dto.InterviewTypeReq;
import com.hrms.Recuirment.dto.InterviewTypeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recuirment")
public class InterviewTypeController {
    @Autowired
    private InterviewTypeUseCase interviewTypeUseCase;

    @PostMapping("saveInterviewType")
    public String createInterviewType(@RequestBody InterviewTypeReq createReq){
        return interviewTypeUseCase.saveInterviewType(createReq);
    }
    @GetMapping("getAllInterviewType")
    public List<InterviewTypeResponse>getAllInterviewType(){
        List<InterviewTypeResponse>res=interviewTypeUseCase.getAllInterviewType();
        return res;
    }
    @GetMapping("getInterviewTypeByInterviewModeId/{id}")
    public List<InterviewTypeResponse>interviewTypeByInterviewMode(@PathVariable Long id){
        List<InterviewTypeResponse>res=interviewTypeUseCase.getInterviewTypeByInterviewMode(id);
        return res;
    }
    @PutMapping("updateInterviewTypeById/{id}")
    public InterviewTypeResponse updateById(@PathVariable Long id ,@RequestBody InterviewTypeReq updatedData){
        InterviewTypeResponse update=interviewTypeUseCase.updateInterviewTypeById(id,updatedData);
        return update;
    }
    @PutMapping("updateInterviewTypeStatus/{id}")
    public String updateInterviewTypeStatus(@PathVariable Long id){
        String updateStatus=interviewTypeUseCase.updateInterviewTypeStatus(id);
        return updateStatus;
    }

}
