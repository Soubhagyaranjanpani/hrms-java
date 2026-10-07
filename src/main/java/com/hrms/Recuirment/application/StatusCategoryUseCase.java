package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.StatusCategory;
import com.hrms.Recuirment.dto.StatusCategoryCreateReq;
import com.hrms.Recuirment.dto.StatusCategoryResponse;
import com.hrms.Recuirment.infrastructure.StatusCategoryRepository;
import com.hrms.common.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusCategoryUseCase {
    @Autowired
    private final StatusCategoryRepository statusCategoryRepository;
    @Autowired
    private final CurrentUser currentUser;

    public String saveStatusCategory(StatusCategoryCreateReq createReq) {
        StatusCategory createObj=new StatusCategory();
        createObj.setCategoryName(createReq.getCategoryName());
        createObj.setStatus("y");
        createObj.setCreatedAt(LocalDateTime.now());
        createObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        StatusCategory save=statusCategoryRepository.save(createObj);
        return "StatusCategory saved successfully";
    }
    public List<StatusCategoryResponse> getAllStatusCategory() {
        List<StatusCategoryResponse>statusCategoryList=statusCategoryRepository.findAll().stream().map(this::todto).toList();
        return statusCategoryList;
    }

    public StatusCategoryResponse updateById(Long id,StatusCategoryCreateReq updatedData) {
        StatusCategory existingData=statusCategoryRepository.findById(id).get();
        if(existingData!=null){
            existingData.setCategoryName(updatedData.getCategoryName());
            existingData.setStatus(updatedData.getStatus());
            StatusCategory res=statusCategoryRepository.save(existingData);
            return todto(res);
        }else {
            return null;
        }
    }
    public String updateStatusCategoryStatus(Long id) {
        Optional<StatusCategory>optionalData=statusCategoryRepository.findById(id);
        if(optionalData.isPresent()){
            StatusCategory existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            statusCategoryRepository.save(existingData);
            return "update statusCategory successfully ";
        }else {
            return "statusCategory not found";
        }
    }
    private StatusCategoryResponse todto(StatusCategory statusCategory) {
        StatusCategoryResponse res=new StatusCategoryResponse();
        res.setId(statusCategory.getId());
        res.setCategoryName(statusCategory.getCategoryName());
        res.setStatus(statusCategory.getStatus());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        return res;
    }
    private StatusCategory toentity(StatusCategoryResponse dto){
        StatusCategory res=new StatusCategory();
        res.setCategoryName(dto.getCategoryName());
        res.setStatus(dto.getStatus());
        res.setCreatedAt(LocalDateTime.now());
        res.setCreatedBy(currentUser.getEmployee().getFirstName());
        return res;
    }

}


