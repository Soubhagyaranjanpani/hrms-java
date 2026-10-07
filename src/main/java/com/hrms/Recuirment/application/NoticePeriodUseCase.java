package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.NoticePeriod;
import com.hrms.Recuirment.dto.NoticePeriodCreateReq;
import com.hrms.Recuirment.dto.NoticePeriodResponce;
import com.hrms.Recuirment.infrastructure.NoticePeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class NoticePeriodUseCase {
    @Autowired
    private NoticePeriodRepository npRepo;
    public String saveNoticePeriod(NoticePeriodCreateReq createReq){
        NoticePeriod createdReqObj=new NoticePeriod();
        createdReqObj.setPeriodCode(createReq.getPeriodCode());
        createdReqObj.setPeriodName(createReq.getPeriodName());
        createdReqObj.setDays(createReq.getDays());
        createdReqObj.setDescription(createReq.getDescription());
        createdReqObj.setStatus("Y");
        npRepo.save(createdReqObj);
        return "save successfully";
    }

    public List<NoticePeriodResponce> getAllNoticePeriod() {
        List<NoticePeriodResponce>noticePeriodList=npRepo.findAll().stream().map(this::toDto).toList();
        return noticePeriodList;
    }

    public NoticePeriodResponce getById(Long id) {
        Optional<NoticePeriodResponce>res=npRepo.findById(id).stream().map(this::toDto).findFirst();
        return res.get();
    }

    public NoticePeriodResponce updateById(Long id, NoticePeriodCreateReq updatedData) {
        NoticePeriod existingData=npRepo.findById(id).get();
        if(existingData!=null){
            existingData.setPeriodCode(updatedData.getPeriodCode());
            existingData.setPeriodName(updatedData.getPeriodName());
            existingData.setDescription(updatedData.getDescription());
            existingData.setDays(updatedData.getDays());
            NoticePeriod res=npRepo.save(existingData);
            return toDto(res);
        }else {
            return null;
        }
    }
    public String updateStatusById(Long id) {
        Optional<NoticePeriod> optionalData = npRepo.findById(id);

        if (optionalData.isPresent()) {
            NoticePeriod existingData = optionalData.get();

            if ("y".equals(existingData.getStatus())) {
                existingData.setStatus("n");
            } else {
                existingData.setStatus("y");
            }

            npRepo.save(existingData);

            return "Status updated successfully";
        } else {
            return "NoticePeriod not found";
        }
    }
    public NoticePeriodResponce toDto (NoticePeriod noticePeriod){
        NoticePeriodResponce res =new NoticePeriodResponce();
        res.setId(noticePeriod.getId());
        res.setPeriodCode(noticePeriod.getPeriodCode());
        res.setPeriodName(noticePeriod.getPeriodName());
        res.setDays(noticePeriod.getDays());
        res.setDescription(noticePeriod.getDescription());
        res.setStatus(noticePeriod.getStatus());
        res.setLastChangeBy(noticePeriod.getCreatedBy());
        res.setLastChangeAt(noticePeriod.getCreatedAt());
        return res;
    }
    public NoticePeriod toentity(NoticePeriodResponce noticePeriod){
        NoticePeriod res=new NoticePeriod();
        res.setId(noticePeriod.getId());
        res.setPeriodCode(noticePeriod.getPeriodCode());
        res.setPeriodName(noticePeriod.getPeriodName());
        res.setDescription(noticePeriod.getDescription());
        res.setDays(noticePeriod.getDays());
        res.setStatus(noticePeriod.getStatus());
        return res;

    }

}
