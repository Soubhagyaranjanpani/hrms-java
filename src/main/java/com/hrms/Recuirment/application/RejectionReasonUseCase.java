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


    public String saveRejectionReason(
            RejectionReasonReq rejectionReasonReq) {

        // Check duplicate reason code
        if (rejectionReasonRepository.existsByReasonCode(rejectionReasonReq.getReasonCode())) {
            throw new RuntimeException("Rejection reason code already exists: " + rejectionReasonReq.getReasonCode());
        }

        // Find reason category
        ReasonCategory reasonCategory = reasonCategoryRepository.findById(rejectionReasonReq.getReasonCategoryId())
                        .orElseThrow(() -> new RuntimeException("ReasonCategory not found"));

//        // Check logged-in employee
//        if (currentUser == null
//                || currentUser.getEmployee() == null) {
//
//            throw new RuntimeException(
//                    "Logged-in employee not found");
//        }

        RejectionReason createObj = new RejectionReason();

        createObj.setReasonCode(rejectionReasonReq.getReasonCode());

        createObj.setReasonName(rejectionReasonReq.getReasonName());

        createObj.setDescription(rejectionReasonReq.getDescription());

        createObj.setStatus("y");

        createObj.setReasonCategory(reasonCategory);

        createObj.setCreatedAt(LocalDateTime.now());

        createObj.setCreatedBy(currentUser.getEmployee().getFirstName());

        rejectionReasonRepository.save(createObj);

        return "Rejection reason saved successfully";
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

        // Find the existing rejection reason
        RejectionReason existingData = rejectionReasonRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Rejection reason not found: " + id));

        //  Check whether the reason code belongs to another record
        boolean codeAlreadyExists =
                rejectionReasonRepository.existsByReasonCodeAndIdNot(
                        updatedData.getReasonCode(),
                        id
                );

        if (codeAlreadyExists) {
            throw new RuntimeException(
                    "Reason code already exists: "
                            + updatedData.getReasonCode()
            );
        }

        // Update the basic fields
        existingData.setReasonCode(updatedData.getReasonCode());

        existingData.setReasonName(updatedData.getReasonName());

        existingData.setDescription(updatedData.getDescription());

        // Find the reason category
        ReasonCategory reasonCategory = reasonCategoryRepository.findById(updatedData.getReasonCategoryId())
                        .orElseThrow(() ->
                                new RuntimeException("Reason category not found: "
                                                + updatedData.getReasonCategoryId()));

        //  Update the category
        existingData.setReasonCategory(reasonCategory);

        //  Save the updated record
        RejectionReason savedData = rejectionReasonRepository.save(existingData);

        //  Convert the saved entity to response DTO
        return todto(savedData);
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
        res.setReasonCategory(rejectionReason.getReasonCategory().getReasonCategoryName());
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
