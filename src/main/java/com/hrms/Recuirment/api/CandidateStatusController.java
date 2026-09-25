package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.CandidateStatusUseCase;
import com.hrms.Recuirment.dto.CandidateStatusReq;
import com.hrms.Recuirment.dto.CandidateStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recuirment")
public class CandidateStatusController {
    @Autowired
    private CandidateStatusUseCase candidateStatusUseCase;

    @PostMapping("saveCandidateStatus")
    public String saveCandidateStatus(@RequestBody CandidateStatusReq statusReq){
        return candidateStatusUseCase.saveCandidateStatus(statusReq);
    }

    @GetMapping("getAllCandidateStatus")
    public List<CandidateStatusResponse>getAllCandidateStatus(){
        List<CandidateStatusResponse>res=candidateStatusUseCase.getAllCandidateStatus();
        return res;
    }

    @PutMapping("updateCandidateStatusById/{id}")
    public CandidateStatusResponse updateById(@PathVariable Long id, @RequestBody CandidateStatusReq updatedData){
        CandidateStatusResponse update=candidateStatusUseCase.updateCandidateStatusById(id,updatedData);
        return update;
    }

    @PutMapping("updateStatusOfCandidateStatus/{id}")
    public String updateStatus(@PathVariable Long id){
        String updateStatus=candidateStatusUseCase.updateStatusOfCandidateStatus(id);
        return updateStatus;
    }

}
