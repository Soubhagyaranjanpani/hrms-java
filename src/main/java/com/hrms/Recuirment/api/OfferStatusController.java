package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.OfferStatusUseCase;
import com.hrms.Recuirment.domain.OfferStatus;
import com.hrms.Recuirment.dto.OfferStatusCreateReq;
import com.hrms.Recuirment.dto.OfferStatusResponce;
import com.hrms.Recuirment.infrastructure.OfferStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recuirment")
public class OfferStatusController {
    @Autowired
    private OfferStatusUseCase useCase;
    @Autowired
    private OfferStatusRepository osRepo;

    @PostMapping("/saveOfferStatus")
    public String saveOfferStatus(@RequestBody OfferStatusCreateReq crr){
        useCase.saveOfferStatus(crr);
        return "save";
    }

    @GetMapping("/fetchallOfferStatus")
    public List<OfferStatusResponce> getAllOfferStatus(){
        List<OfferStatusResponce> re = useCase.getAllOfferStatus();
        return re;
    }


    @GetMapping("/fetchByIdOfferStatus/{id}")
    public OfferStatusResponce getOfferStatus(@PathVariable Long id){
        OfferStatusResponce offerStatus= useCase.getById(id);
        return offerStatus;
    }


    @PutMapping("/updateOfferStatus/{id}")
    public OfferStatusResponce updateOfferStatus(@PathVariable Long id,@RequestBody OfferStatusCreateReq updatedData){
        OfferStatusResponce update=useCase.updateById(id,updatedData);
        return update;

    }

    @PutMapping("/updateOfferStatusStatus/{id}")
    public String updateStatusById(@PathVariable Long id){
        String update=useCase.updateStatusById(id);
        return update;

    }
}
