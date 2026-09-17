package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.WorkMode;
import com.hrms.Recuirment.dto.WorkModeCreateReq;
import com.hrms.Recuirment.dto.WorkModeResponse;
import com.hrms.Recuirment.infrastructure.WorkModeRepository;
import com.hrms.common.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkModeUseCase {
    @Autowired
    private final WorkModeRepository workModeRepository;
    @Autowired
    private final CurrentUser currentUser;

    public String saveWorkMode(WorkModeCreateReq createReq){
        WorkMode createReqObj=new WorkMode();
        createReqObj.setId(createReqObj.getId());
        createReqObj.setWorkModeName(createReq.getWorkModeName());
        createReqObj.setStatus("Y");
        createReqObj.setCreatedAt(LocalDateTime.now());
        createReqObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        WorkMode save=workModeRepository.save(createReqObj);
        return "workMode save successfully";
    }

    public List<WorkModeResponse> getAllWorkMode() {
        List<WorkModeResponse>workModeList=workModeRepository.findAll().stream().map(this::todto).toList();
        return workModeList;
    }

    public WorkModeResponse updateById(Long id, WorkModeCreateReq updatedData) {
        WorkMode existingData=workModeRepository.findById(id)
                                               .orElseThrow(()->new RuntimeException("workMode not found"+id));
        if(existingData!=null){
            existingData.setWorkModeName(updatedData.getWorkModeName());
            existingData.setStatus(updatedData.getStatus());
            WorkMode savedData=workModeRepository.save(existingData);
            return todto(savedData);
        }else {
            return null;
        }
    }

    public String updateStatusById(Long id) {
        Optional<WorkMode> optionalData = workModeRepository.findById(id);
        if (optionalData.isPresent()) {
            WorkMode existingData = optionalData.get();
            if ("y".equals(existingData.getStatus())) {
                existingData.setStatus("n");
            } else {
                existingData.setStatus("y");
            }
            workModeRepository.save(existingData);
            return "update status successfully";
        } else {
            return "workMode not found";
        }
    }
    public WorkModeResponse todto(WorkMode workMode) {
        WorkModeResponse res=new WorkModeResponse();
        res.setId(workMode.getId());
        res.setWorkModeName(workMode.getWorkModeName());
        res.setStatus(workMode.getStatus());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        return res;
    }
    public WorkMode toentity(WorkModeResponse dto){
        WorkMode res=new WorkMode();
        res.setWorkModeName(dto.getWorkModeName());
        res.setStatus(dto.getStatus());
        return res;
    }
}



