package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.InterviewRoundUseCase;

import com.hrms.Recuirment.dto.InterviewRoundReq;
import com.hrms.Recuirment.dto.InterviewRoundResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recuirment")
public class InterviewRoundController {
    @Autowired
    private  InterviewRoundUseCase interviewRoundUseCase;

    @PostMapping("saveInterviewRound")
    public String createInterviewRound(@RequestBody InterviewRoundReq createReq){
        return interviewRoundUseCase.saveInterviewRound(createReq);
    }
    @GetMapping("getAllInterviewRound")
    public List<InterviewRoundResponse>getAllInterviewRound(){
        List<InterviewRoundResponse>res=interviewRoundUseCase.getAllInterviewRound();
        return res;
    }
    @GetMapping("getInterviewRoundByDepartment/{id}")
    public List<InterviewRoundResponse>inetrviewRoundByDepartment(@PathVariable Long id){
        List<InterviewRoundResponse>res=interviewRoundUseCase.getInterviewRoundByDepartmentId(id);
        return res;
    }
    @PutMapping("updateInterviewRoundById/{id}")
    public InterviewRoundResponse updateById(@PathVariable Long id,@RequestBody InterviewRoundReq updatedData){
        InterviewRoundResponse update=interviewRoundUseCase.updateInterviewRoundById(id,updatedData);
        return update;
    }
    @PutMapping("updateInterviewRoundStatus/{id}")
    public String updateStatus(@PathVariable Long id){
        String updateStatus=interviewRoundUseCase.updateInterviewRoundStatus(id);
        return updateStatus;
    }
}
