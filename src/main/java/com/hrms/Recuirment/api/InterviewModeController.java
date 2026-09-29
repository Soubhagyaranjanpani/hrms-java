package com.hrms.Recuirment.api;


import com.hrms.Recuirment.application.InterviewModeUseCase;
import com.hrms.Recuirment.dto.InterviewModeReq;
import com.hrms.Recuirment.dto.InterviewModeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/interviewMode")
public class InterviewModeController {

    @Autowired
    private InterviewModeUseCase interviewModeUseCase;

    @PostMapping("/create")
    public  String createInterviewMode(@RequestBody InterviewModeReq createReq){
         return interviewModeUseCase.createInterviewMode(createReq);

    }


    @GetMapping("/getAllInterviewMode")
    public List<InterviewModeResponse> getAllInterviewMode(){
        List<InterviewModeResponse> res=interviewModeUseCase.getAllInterviewMode();
        return res;
    }

    @PutMapping("/updateInterviewMode/{id}")
    public InterviewModeResponse updateInterview(@PathVariable Long id, @RequestBody InterviewModeReq updateInterviewMode){
        InterviewModeResponse update=interviewModeUseCase.updateById(id,updateInterviewMode);
        return  update;
    }


    @PutMapping("/updateInterviewModeStatus/{id}")
    public String updateStatusInterviewModeById(@PathVariable Long id){
        String update=interviewModeUseCase.updateStatusById(id);
        return  update;
    }



}
