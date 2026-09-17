package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.CityUseCase;
import com.hrms.Recuirment.dto.CityCreateReq;
import com.hrms.Recuirment.dto.CityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recuirment")
public class CityController {
    @Autowired
    private CityUseCase cityUseCase;
    @PostMapping("/saveCity")
    public String saveCity(@RequestBody CityCreateReq createReq){
        return cityUseCase.saveCity(createReq);
    }

    @GetMapping("/getAllCity")
    public List<CityResponse>getAllCity(){
        List<CityResponse>res=cityUseCase.getAllCity();
        return res;
    }

    @GetMapping("/getCityByStateId/{id}")
    public List<CityResponse>cityByState(@PathVariable Long id){
        List<CityResponse>res=cityUseCase.getCityByStateId(id);
        return res;
    }

    @PutMapping("/updateCityById/{id}")
    public CityResponse updateCity(@PathVariable Long id,@RequestBody CityCreateReq updatedData){
        CityResponse update=cityUseCase.updateById(id,updatedData);
        return update;
    }

    @PutMapping("/updateCityStatus/{id}")
    public String updateCity(@PathVariable Long id){
        String update=cityUseCase.updateStatusById(id);
        return update;
    }

}
