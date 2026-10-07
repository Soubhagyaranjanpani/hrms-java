package com.hrms.Recuirment.api;

import com.hrms.Recuirment.application.CountryUseCase;
import com.hrms.Recuirment.dto.CountryCreateReq;
import com.hrms.Recuirment.dto.CountryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recuirment")
public class CountryController {
    @Autowired
    private CountryUseCase useCase;
    @PostMapping("/saveCountry")
    public String saveCountry(@RequestBody CountryCreateReq createReq){
        useCase.saveCountry(createReq);
        return "save";
    }
    @GetMapping("/fetchallCountry")
    public List<CountryResponse> getAllCountryDetails(){
        List<CountryResponse> res=useCase.getAllCountryDetails();
        return res;
    }
    @GetMapping("/fetchByIdCountry/{id}")
    public CountryResponse getCountryDetailById(@PathVariable Long id){
        CountryResponse country=useCase.getById(id);
        return country;
    }
    @PutMapping("/updateCountryById/{id}")
    public CountryResponse updateCountry(@PathVariable Long id,@RequestBody CountryCreateReq updatedData){
        CountryResponse update=useCase.updateById(id,updatedData);
        return update;
    }
    @PutMapping("/updateCountryStatus/{id}")
    public String updateStatusCountryById(@PathVariable Long id){
       String update=useCase.updateStatusById(id);
       return update;
    }
}
