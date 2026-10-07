package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.RejectionReasonUseCase;
import com.hrms.Recuirment.dto.RejectionReasonReq;
import com.hrms.Recuirment.dto.RejectionReasonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recuirment")
public class RejectionReasonController {
    @Autowired
    private RejectionReasonUseCase rejectionReasonUseCase;

    @PostMapping("saveRejectionReason")
    public String saveRejectionReason(@RequestBody RejectionReasonReq rejectionReasonReq){
        return rejectionReasonUseCase.saveRejectionReason(rejectionReasonReq);
    }

    @GetMapping("getAllRejectionReason")
    public List<RejectionReasonResponse>getAllRejectionReason(){
        List<RejectionReasonResponse>res=rejectionReasonUseCase.getAllRejectionReason();
        return res;
    }

    @GetMapping("getRejectionReasonByReasonCategory/{id}")
    public List<RejectionReasonResponse> getRejectionReasonByReasonCategory(@PathVariable Long id){
        List<RejectionReasonResponse>res=rejectionReasonUseCase.getRejectionReasonByReasonCategoryId(id);
        return res;
    }

    @PutMapping("updateRejectionReasonById/{id}")
    public RejectionReasonResponse updateRejectionReasonById(@PathVariable Long id,@RequestBody RejectionReasonReq updatedData){
        RejectionReasonResponse update=rejectionReasonUseCase.updateRejectionReason(id,updatedData);
        return update;
    }

    @PutMapping("updateRejectionReasonStatus/{id}")
    public String updateRejectionReasonStatus(@PathVariable Long id){
        String update=rejectionReasonUseCase.updateStatusById(id);
        return update;
    }
}
