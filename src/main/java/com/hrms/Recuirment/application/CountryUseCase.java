package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.Country;
import com.hrms.Recuirment.dto.CountryCreateReq;
import com.hrms.Recuirment.dto.CountryResponse;
import com.hrms.Recuirment.infrastructure.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryUseCase {
    @Autowired
    private CountryRepository countryRepository;
    public String saveCountry(CountryCreateReq createReq){
        Country createReqObj=new Country();
        createReqObj.setCountryCode(createReq.getCountryCode());
        createReqObj.setCountryName(createReq.getCountryName());
        createReqObj.setStatus("Y");
        countryRepository.save(createReqObj);
        return "save successfully";
    }
    public List<CountryResponse> getAllCountryDetails() {
        List<CountryResponse>countryList=countryRepository.findAll().stream().map(this::todto).toList();
        return countryList;
    }
    public CountryResponse getById(Long id){
        Optional<CountryResponse>res=countryRepository.findById(id).stream().map(this::todto).findFirst();
        return res.get();
    }
    public CountryResponse updateById(Long id, CountryCreateReq updatedData) {
        Country existingData=countryRepository.findById(id).get();
        if(existingData!=null){
            existingData.setCountryCode(updatedData.getCountryCode());
            existingData.setCountryName(updatedData.getCountryName());
            Country res=countryRepository.save(existingData);
            return todto(res);
        }else {
            return null;
        }
    }
    public String updateStatusById(Long id) {
        Optional<Country>optionalData=countryRepository.findById(id);
        if(optionalData.isPresent()){
            Country existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            countryRepository.save(existingData);

            return "status update successfully";
        }else {
            return "country not found !";
        }
    }

    public CountryResponse todto(Country country) {
        CountryResponse res=new CountryResponse();
        res.setId(country.getId());
        res.setCountryCode(country.getCountryCode());
        res.setCountryName(country.getCountryName());
        res.setStatus(country.getStatus());
        res.setLastChangeAt(country.getCreatedAt());
        res.setLastChangeBy(country.getCreatedBy());
        return res;
    }
    public Country toentity(CountryResponse country){
        Country res=new Country();
        res.setId(country.getId());
        res.setCountryCode(country.getCountryCode());
        res.setCountryName(country.getCountryName());
        res.setStatus(country.getStatus());
        return res;
    }
    //

}
