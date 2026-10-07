package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.OfferStatus;
import com.hrms.Recuirment.dto.OfferStatusCreateReq;
import com.hrms.Recuirment.dto.OfferStatusResponce;
import com.hrms.Recuirment.infrastructure.OfferStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferStatusUseCase {
    @Autowired
    private OfferStatusRepository osRepo;
    public String saveOfferStatus(OfferStatusCreateReq createReq){
        OfferStatus createReqObj = new OfferStatus();
        createReqObj.setStatusCode(createReq.getStatusCode());
        createReqObj.setStatusName(createReq.getStatusName());
        createReqObj.setDescription(createReq.getDescription());
        createReqObj.setStatus("y");
        osRepo.save(createReqObj);
        return "save successfully";
    }


    public List<OfferStatusResponce> getAllOfferStatus() {
        List <OfferStatusResponce> offerStatusList=osRepo.findAll().stream().map(this::toDto).toList();

        return offerStatusList;
    }


    public OfferStatusResponce getById(Long id) {

        Optional<OfferStatusResponce> res =osRepo.findById(id).stream().map(this::toDto).findFirst();
        return res.get();

    }


    public OfferStatusResponce updateById(Long id, OfferStatusCreateReq updatedData) {

        OfferStatus existingData = osRepo.findById(id).get();

        if(existingData!=null){
            existingData.setStatusCode(updatedData.getStatusCode());
            existingData.setStatusName(updatedData.getStatusName());
            existingData.setDescription(updatedData.getDescription());

            OfferStatus ff= osRepo.save(existingData);
            return toDto(ff);
        }else{
            return null;
        }
    }

    public String updateStatusById(Long id) {

        Optional<OfferStatus> optionalData = osRepo.findById(id);

        if (optionalData.isPresent()) {

            OfferStatus existingData = optionalData.get();

            if ("y".equals(existingData.getStatus())) {
                existingData.setStatus("n");
            } else {
                existingData.setStatus("y");
            }

            osRepo.save(existingData);

            return "Status updated successfully";
        } else {
            return "OfferStatus not found";
        }
    }
    public OfferStatusResponce toDto (OfferStatus offerStatus){
        OfferStatusResponce res = new OfferStatusResponce();
        res.setId(offerStatus.getId());
        res.setStatusCode(offerStatus.getStatusCode());
        res.setStatusName(offerStatus.getStatusName());
        res.setDescription(offerStatus.getDescription());
        res.setStatus(offerStatus.getStatus());
        res.setLastChangeBy(offerStatus.getCreatedBy());
        res.setLastchangeAt(offerStatus.getCreatedAt());
        return res;
    }


    public OfferStatus toentity (OfferStatusResponce offerStatus){
        OfferStatus res = new OfferStatus();
        res.setId(offerStatus.getId());
        res.setStatusCode(offerStatus.getStatusCode());
        res.setStatusName(offerStatus.getStatusName());
        res.setDescription(offerStatus.getDescription());
        res.setStatus(offerStatus.getStatus());
        return res;
    }


}
