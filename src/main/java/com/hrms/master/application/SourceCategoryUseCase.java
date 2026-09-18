package com.hrms.master.application;

import com.hrms.common.utils.CurrentUser;
import com.hrms.master.domain.SourceCategory;
import com.hrms.master.dto.SourceCategoryCreateReq;
import com.hrms.master.dto.SourceCategoryResponse;
import com.hrms.master.infrastructure.SourceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceCategoryUseCase {
    @Autowired
    private  final  SourceCategoryRepository sourceCategoryRepository;
    @Autowired
    private final CurrentUser currentUser;



    public String createSourceCategory(SourceCategoryCreateReq request){
        SourceCategory createObj=new SourceCategory();
        createObj.setName(request.getName());
        createObj.setStatus("y");
        createObj.setCreatedAt(LocalDateTime.now());
        createObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        SourceCategory save=sourceCategoryRepository.save(createObj);
        return "source category save sucessfully";

    }



    public List<SourceCategoryResponse> getAllSourceCategory(){


        List<SourceCategory> list = sourceCategoryRepository.findAll();


        return  list.stream().map(entity ->{

            SourceCategoryResponse response = new SourceCategoryResponse();


            response.setId(entity.getId());
            response.setName(entity.getName());
            response.setActive(entity.getActive());
            response.setCreatedBy(entity.getCreatedBy());

            return  response;
        }).toList();
    }




    public  SourceCategoryResponse updateSourceCategory(
            Long id,
            SourceCategoryCreateReq request){

        SourceCategory entity = sourceCategoryRepository.findById(id)


                .orElseThrow(()->
                         new RuntimeException("Source Category not found"));


        entity.setName(request.getName());

        SourceCategory updated = sourceCategoryRepository.save(entity);

        SourceCategoryResponse response = new SourceCategoryResponse();

        response.setId(updated.getId());
        response.setName(updated.getName());
        response.setActive(updated.getActive());
        response.setCreatedBy(updated.getCreatedBy());

        return  response;

    }




    public String changeStatus(Long id){
        SourceCategory entity =
                sourceCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Source Category not found"));

        entity.setActive(!entity.getActive());

        sourceCategoryRepository.save(entity);

        return "Status updated successfully";

    }
}
