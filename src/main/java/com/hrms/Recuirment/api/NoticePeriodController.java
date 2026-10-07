package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.NoticePeriodUseCase;
import com.hrms.Recuirment.dto.NoticePeriodCreateReq;
import com.hrms.Recuirment.dto.NoticePeriodResponce;
import com.hrms.Recuirment.infrastructure.NoticePeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/recuirment")
public class NoticePeriodController {
    @Autowired
    private NoticePeriodUseCase useCase;
    @Autowired
    private NoticePeriodRepository npRepo;
    @PostMapping("/saveNoticePeriod")
    public String saveNoticePeriod(@RequestBody NoticePeriodCreateReq crr){
        useCase.saveNoticePeriod(crr);
        return "save";
    }
    @GetMapping("/fetchallNoticePeriod")
    public List<NoticePeriodResponce>getAllNoticePeriod(){
        List<NoticePeriodResponce> res=useCase.getAllNoticePeriod();
        return res;
    }
    @GetMapping("/fetchByIdNoticePeriod/{id}")
    public NoticePeriodResponce getNoticePeriod(@PathVariable Long id){
        NoticePeriodResponce noticePeriod =useCase.getById(id);
        return noticePeriod;
    }
    @PutMapping("/updateNoticePeriod/{id}")
    public NoticePeriodResponce updateNoticePeriod(@PathVariable Long id,@RequestBody NoticePeriodCreateReq updatedData){
        NoticePeriodResponce update=useCase.updateById(id,updatedData);
        return update;
    }
    @PutMapping("/updateNoticePeriodPeriod/{id}")
    public String updateNoticePeriodId(@PathVariable Long id){
        String update=useCase.updateStatusById(id);
        return update;
    }
}
