package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.ReasonCategory;
import com.hrms.Recuirment.dto.ReasonCategoryReq;
import com.hrms.Recuirment.dto.ReasonCategoryResponse;
import com.hrms.Recuirment.infrastructure.ReasonCategoryRepository;
import com.hrms.common.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReasonCategoryUseCase {
    @Autowired
    private final ReasonCategoryRepository reasonCategoryRepository;
    @Autowired
    private final CurrentUser currentUser;

    public String saveReasonCategory(ReasonCategoryReq categoryReq){
        ReasonCategory createObJ=new ReasonCategory();
        createObJ.setReasonCategoryName(categoryReq.getReasonCategoryName());
        createObJ.setStatus("Y");
        createObJ.setCreatedAt(LocalDateTime.now());
        createObJ.setCreatedBy(currentUser.getEmployee().getFirstName());
        ReasonCategory save=reasonCategoryRepository.save(createObJ);
        return "ReasonCategory save successfully";
    }
    public List<ReasonCategoryResponse> getAllReasonCategory() {
        List<ReasonCategoryResponse>reasonCategoryList=reasonCategoryRepository.findAll().stream().map(this::todto).toList();
        return reasonCategoryList;
    }
    public ReasonCategoryResponse updateReasonCategoryById(Long id, ReasonCategory updatedData) {
        ReasonCategory existingData=reasonCategoryRepository.findById(id).get();
        if(existingData!=null){
            existingData.setReasonCategoryName(updatedData.getReasonCategoryName());
            ReasonCategory res=reasonCategoryRepository.save(existingData);
            return todto(res);
        }else {
            return null;
        }
    }
    public String updateReasonCategoryStatus(Long id) {
        Optional<ReasonCategory>optionalDate=reasonCategoryRepository.findById(id);
        if(optionalDate.isPresent()){
            ReasonCategory existingData=optionalDate.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            reasonCategoryRepository.save(existingData);
            return "status updated successfully";
        }else {
            return "ReasonCategory not found";
        }
    }
    private ReasonCategoryResponse todto(ReasonCategory reasonCategory) {
        ReasonCategoryResponse res=new ReasonCategoryResponse();
        res.setId(reasonCategory.getId());
        res.setReasonCategoryName(reasonCategory.getReasonCategoryName());
        res.setStatus(reasonCategory.getStatus());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        return res;
    }
    private ReasonCategory toentity(ReasonCategoryResponse dto){
        ReasonCategory res=new ReasonCategory();
        res.setId(dto.getId());
        res.setReasonCategoryName(dto.getReasonCategoryName());
        res.setStatus(dto.getStatus());
        return res;
    }
}
