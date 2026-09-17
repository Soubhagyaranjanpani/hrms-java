package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.City;
import com.hrms.Recuirment.domain.State;
import com.hrms.Recuirment.dto.CityCreateReq;
import com.hrms.Recuirment.dto.CityResponse;
import com.hrms.Recuirment.infrastructure.CityRepository;
import com.hrms.Recuirment.infrastructure.StateRepository;
import com.hrms.common.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityUseCase {
    @Autowired
    private final CityRepository cityRepository;
    @Autowired
    private final StateRepository stateRepository;

    @Autowired
    private final CurrentUser currentUser;
    public String saveCity(CityCreateReq createReq) {
        State state=stateRepository.findById(createReq.getStateId())
                                   .orElseThrow(()->new RuntimeException("city not found"));
        City createReqObj=new City();
        createReqObj.setCityCode(createReq.getCityCode());
        createReqObj.setCityName(createReq.getCityName());
        createReqObj.setStatus("Y");
        createReqObj.setState(state);
        createReqObj.setCreatedAt(LocalDateTime.now());
        createReqObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        City save=cityRepository.save(createReqObj);

        return " City save successfully";
    }
    public List<CityResponse> getAllCity() {
        List<CityResponse>cityList=cityRepository.findAll().stream().map(this::todto).toList();
        return cityList;
    }
    public List<CityResponse> getCityByStateId(Long id) {
        List<CityResponse>city=cityRepository.findByStateId(id).stream().map(this::todto).toList();
        return city;
    }
    public CityResponse updateById(Long id, CityCreateReq updatedData) {
        City existingData=cityRepository.findById(id)
                .orElseThrow(()->new RuntimeException("city not found with id"+id));
        if(existingData!=null){
            existingData.setCityCode(updatedData.getCityCode());
            existingData.setCityName(updatedData.getCityName());

            //get state using stateId
            State state=stateRepository.findById(id)
                    .orElseThrow(()->new RuntimeException("state not found with id"+updatedData.getStateId()));
            if(state!=null){
                existingData.setState(state);
            }
            City savedData=cityRepository.save(existingData);
            return todto(savedData);
        }else {
            return null;
        }
    }
    public String updateStatusById(Long id) {
        Optional<City> optionalData=cityRepository.findById(id);
        if(optionalData.isPresent()){
            City existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            cityRepository.save(existingData);
            return "update status successfully";
        }else {
            return "city not found";
        }
    }
    public CityResponse todto(City city) {
        CityResponse res=new CityResponse();
        res.setId(city.getId());
        res.setCityCode(city.getCityCode());
        res.setCityName(city.getCityName());
        res.setStatus(city.getStatus());
        res.setStateId(city.getState().getId());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        return res;
    }
    public City toentity(CityResponse dto){
        City res=new City();
        res.setId(dto.getStateId());
        res.setCityCode(dto.getCityCode());
        res.setCityName(dto.getCityName());
        res.setStatus(dto.getStatus());

        State state=new State();
        state.setId(dto.getStateId());
        res.setState(state);
        return res;
    }
}
