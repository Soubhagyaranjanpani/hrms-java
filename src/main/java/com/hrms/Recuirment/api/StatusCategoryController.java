package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.StatusCategoryUseCase;
import com.hrms.Recuirment.dto.StatusCategoryCreateReq;
import com.hrms.Recuirment.dto.StatusCategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recuirment")
public class StatusCategoryController {
    @Autowired
    private  StatusCategoryUseCase statusCategoryUseCase;

    @PostMapping("saveStatusCategory")
    public String saveStatusCategory(@RequestBody StatusCategoryCreateReq createReq){
         statusCategoryUseCase.saveStatusCategory(createReq);
         return "save";
    }

    @GetMapping("getAllStatusCategory")
    public List<StatusCategoryResponse>getAllStatusCategory(){
        List<StatusCategoryResponse>res=statusCategoryUseCase.getAllStatusCategory();
        return res;
    }

    @PutMapping("updateStatusCategoryById/{id}")
    public StatusCategoryResponse updateStatusCategoryById(@PathVariable Long id,@RequestBody StatusCategoryCreateReq updatedData){
        StatusCategoryResponse update=statusCategoryUseCase.updateById(id,updatedData);
        return update;
    }

    @PutMapping("updateStatusCategoryStatus/{id}")
    public String updateStatusCategoryStatus(@PathVariable Long id){
        String update=statusCategoryUseCase.updateStatusCategoryStatus(id);
        return update;
    }

}
