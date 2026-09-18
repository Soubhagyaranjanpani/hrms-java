package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.ReasonCategoryUseCase;
import com.hrms.Recuirment.domain.ReasonCategory;
import com.hrms.Recuirment.dto.ReasonCategoryReq;
import com.hrms.Recuirment.dto.ReasonCategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recuirment")
public class ReasonCategoryController {
    @Autowired
    private ReasonCategoryUseCase reasonCategoryUseCase;
    @PostMapping("/saveReasonCategory")
    public String saveReasonCategory(@RequestBody ReasonCategoryReq categoryReq){
        reasonCategoryUseCase.saveReasonCategory(categoryReq);
        return "save successfully";
    }
    @GetMapping("/getAllReasonCategory")
    public List<ReasonCategoryResponse>getAllReasonCategory(){
        List<ReasonCategoryResponse>res=reasonCategoryUseCase.getAllReasonCategory();
        return res;
    }
    @PutMapping("/updateReasonCategoryById/{id}")
    public ReasonCategoryResponse updateReasonCategory(@PathVariable Long id, @RequestBody ReasonCategory updatedData){
        ReasonCategoryResponse update=reasonCategoryUseCase.updateReasonCategoryById(id,updatedData);
        return update;
    }
    @PutMapping("/updateReasonCategoryStatus/{id}")
    public String updateReasonCategoryStatus(@PathVariable Long id){
        String update=reasonCategoryUseCase.updateReasonCategoryStatus(id);
        return update;
    }
}
