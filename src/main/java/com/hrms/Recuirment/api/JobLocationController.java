package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.JobLocationUseCase;
import com.hrms.Recuirment.dto.JobLocationReq;
import com.hrms.Recuirment.dto.JobLocationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recuirment")
public class JobLocationController {
    @Autowired
    private JobLocationUseCase jobLocationUseCase;

    @PostMapping("saveLocation")
    public String saveLocation(@RequestBody JobLocationReq locationReq){
        return jobLocationUseCase.saveLocation(locationReq);
    }

    @GetMapping("getAllJobLocation")
    public List<JobLocationResponse>getAllJobLocation(){
        List<JobLocationResponse>res=jobLocationUseCase.getJobLocation();
        return res;
    }

    @PutMapping("updateJobLocationById/{id}")
    public JobLocationResponse updateJobLocation(@PathVariable Long id,@RequestBody JobLocationReq updatedData){
        JobLocationResponse update=jobLocationUseCase.updateById(id,updatedData);
        return update;
    }

    @PutMapping("updateJobLocationStatus/{id}")
    public String updateJobLocationId(@PathVariable Long id){
        String update=jobLocationUseCase.updateStatusById(id);
        return update;
    }
}
