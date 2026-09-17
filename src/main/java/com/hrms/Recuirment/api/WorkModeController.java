package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.WorkModeUseCase;
import com.hrms.Recuirment.dto.WorkModeCreateReq;
import com.hrms.Recuirment.dto.WorkModeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recuirment")
public class WorkModeController {
    @Autowired
    private WorkModeUseCase useCase;
    @PostMapping("/saveWorkMode")
    public String saveWorkMode(@RequestBody WorkModeCreateReq createReq){
        return useCase.saveWorkMode(createReq);
    }
    @GetMapping("/getAllWorkMode")
    public List<WorkModeResponse>getAllWorkMode(){
        List<WorkModeResponse>res=useCase.getAllWorkMode();
        return res;
    }
    @PutMapping("/updateWorkModeById/{id}")
    public WorkModeResponse updateWorkMode(@PathVariable Long id ,@RequestBody WorkModeCreateReq updatedData){
         WorkModeResponse update=useCase.updateById(id,updatedData);
         return update;
    }
    @PutMapping("/updateWorkModeStatus/{id}")
    public String updateWorkModeStatus(@PathVariable Long id){
        String update=useCase.updateStatusById(id);
        return update;
    }

}

