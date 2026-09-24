package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.ReasonCategory;
import com.hrms.Recuirment.domain.RejectionReason;
import com.hrms.Recuirment.dto.RejectionReasonReq;
import com.hrms.Recuirment.dto.RejectionReasonResponse;
import com.hrms.Recuirment.infrastructure.ReasonCategoryRepository;
import com.hrms.Recuirment.infrastructure.RejectionReasonRepository;
import com.hrms.common.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RejectionReasonUseCase {
    @Autowired
    private final RejectionReasonRepository rejectionReasonRepository;
    @Autowired
    private final ReasonCategoryRepository reasonCategoryRepository;
    @Autowired
    private final CurrentUser currentUser;


    public String saveRejectionReason(RejectionReasonReq rejectionReasonReq) {

        ReasonCategory reasonCategory=reasonCategoryRepository.findById(rejectionReasonReq.getReasonCategoryId())
                                      .orElseThrow(()->new RuntimeException("ReasonCategory not found"));

        RejectionReason createObj=new RejectionReason();
        createObj.setReasonCode(rejectionReasonReq.getReasonCode());
        createObj.setReasonName(rejectionReasonReq.getReasonName());
        createObj.setDescription(rejectionReasonReq.getDescription());
        createObj.setStatus("y");
        createObj.setReasonCategory(reasonCategory);
        createObj.setCreatedAt(LocalDateTime.now());
        createObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        RejectionReason save=rejectionReasonRepository.save(createObj);
        return "rejectionReason save successfully";
    }
    public List<RejectionReasonResponse> getAllRejectionReason() {
        List<RejectionReasonResponse>rejectionReasonList=rejectionReasonRepository.findAll().stream().map(this::todto).toList();
        return rejectionReasonList;
    }
    public List<RejectionReasonResponse> getRejectionReasonByReasonCategoryId(Long id) {
        List<RejectionReasonResponse>rejectionReason=rejectionReasonRepository.findByReasonCategoryId(id);
        return rejectionReason;
    }
    public RejectionReasonResponse updateRejectionReason(Long id, RejectionReasonReq updatedData) {
        RejectionReason existingData=rejectionReasonRepository.findById(id).orElseThrow(()->new RuntimeException("Rejection reason not found"+id));
        if(existingData!=null){
            //update basic fields
            existingData.setReasonCode(updatedData.getReasonCode());
            existingData.setReasonName(updatedData.getReasonName());
            existingData.setDescription(updatedData.getDescription());

            //get reasonCategory using reasonCategoryId
            ReasonCategory reasonCategory=reasonCategoryRepository.findById(updatedData.getReasonCategoryId())
                                          .orElseThrow(()->new RuntimeException("reasonCategory not found"));
            if(reasonCategory!=null){
                existingData.setReasonCategory(reasonCategory);
            }
            //save updated entity
            RejectionReason savedData=rejectionReasonRepository.save(existingData);
            return todto(savedData);
        }else {
            return null;
        }
    }
    public String updateStatusById(Long id) {
        Optional<RejectionReason>optionalData=rejectionReasonRepository.findById(id);
        if(optionalData.isPresent()){
            RejectionReason existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            rejectionReasonRepository.save(existingData);
            return "update RejectionReason successfully";
        }else {
            return "RejectionReason not found";
        }
    }
    private RejectionReasonResponse todto(RejectionReason rejectionReason) {
        RejectionReasonResponse res=new RejectionReasonResponse();
        res.setId(rejectionReason.getId());
        res.setReasonCode(rejectionReason.getReasonCode());
        res.setReasonName(rejectionReason.getReasonName());
        res.setDescription(rejectionReason.getDescription());
        res.setStatus(rejectionReason.getStatus());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        res.setReasonCategoryId(rejectionReason.getId());
        return res;
    }
    private RejectionReason toentity(RejectionReasonResponse dto){
        RejectionReason res=new RejectionReason();
        res.setId(dto.getId());
        res.setReasonCode(dto.getReasonCode());
        res.setReasonName(dto.getReasonName());
        res.setDescription(dto.getDescription());
        res.setStatus(dto.getStatus());

        ReasonCategory reasonCategory=new ReasonCategory();
        reasonCategory.setId(dto.getId());
        res.setReasonCategory(reasonCategory);
        return res;
    }
}
