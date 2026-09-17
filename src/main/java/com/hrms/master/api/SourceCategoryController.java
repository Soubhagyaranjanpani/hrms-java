package com.hrms.master.api;


import com.hrms.master.application.SourceCategoryUseCase;
import com.hrms.master.dto.SourceCategoryCreateReq;
import com.hrms.master.dto.SourceCategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/source_category")
public class SourceCategoryController {

    @Autowired
    private  SourceCategoryUseCase sourceCategoryUseCase;

    @PostMapping("/create")
    public SourceCategoryResponse createSourceCategory(
            @RequestBody SourceCategoryCreateReq request){

        return sourceCategoryUseCase.createSourceCategory(request);
    }


    @GetMapping("/list")
    public List<SourceCategoryResponse> getAllSourceCategory(){
        return sourceCategoryUseCase.getAllSourceCategory();
    }


    @PutMapping("/update")
    public SourceCategoryResponse updateSourceCategory(@PathVariable Long id,
                                                       @RequestBody SourceCategoryCreateReq request){
        return sourceCategoryUseCase.updateSourceCategory(id, request);
    }


    @PutMapping("/status/{id}")
    public  String changeStatus(@PathVariable Long id){
        return  sourceCategoryUseCase.changeStatus(id);
    }


}
