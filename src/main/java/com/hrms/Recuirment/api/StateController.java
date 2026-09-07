package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.StateUseCase;
import com.hrms.Recuirment.dto.StateCreateReq;
import com.hrms.Recuirment.dto.StateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recuirment")
public class StateController {
    @Autowired
    private StateUseCase useCase;
    @PostMapping("/saveState")
    public String saveState(@RequestBody StateCreateReq createReq){
        return useCase.saveState(createReq);
    }
    @GetMapping("/getAllState")
    public List<StateResponse>getAllState(){
        List<StateResponse>res=useCase.getAllState();
        return res;
    }
    @PutMapping("/updateById/{id}")
    public StateResponse updateState(@PathVariable Long id,@RequestBody StateCreateReq updatedData){
        StateResponse update=useCase.updateById(id,updatedData);
        return update;
    }
    @PutMapping("/updateStateStatus/{id}")
    public String updateStateStatus(@PathVariable Long id){
        String update=useCase.updateStatusById(id);
        return update;
    }

}
