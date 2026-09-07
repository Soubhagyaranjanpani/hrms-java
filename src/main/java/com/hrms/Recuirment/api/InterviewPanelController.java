package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.InterviewPanelUseCase;
import com.hrms.Recuirment.dto.InterviewPanelCreateReq;
import com.hrms.Recuirment.dto.InterviewPanelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recuirment")
public class InterviewPanelController {
    @Autowired
    private InterviewPanelUseCase interviewPanelUseCase;

    @PostMapping("/createInterviewPanel")
    public String createPanel(@RequestBody InterviewPanelCreateReq createReq){
        return  interviewPanelUseCase.createPanel(createReq);
    }

    @GetMapping("/getAllInterviewPanel")
    public List<InterviewPanelResponse>getAllInterviewPanel(){
        List<InterviewPanelResponse>res=interviewPanelUseCase.getInterviewPanel();
        return res;
    }
    @PutMapping("/updateInterviewPanel/{id}")
    public InterviewPanelResponse updateInterviewPanel(@PathVariable Long id,@RequestBody InterviewPanelCreateReq updatedData){
        InterviewPanelResponse update=interviewPanelUseCase.updateById(id, updatedData);
        return update;
    }
    @PutMapping("/updateInterviewPanelStatus/{id}")
    public String updateInterviewPanelId(@PathVariable Long id){
        String update=interviewPanelUseCase.updateStatusById(id);
        return update;
    }

}
