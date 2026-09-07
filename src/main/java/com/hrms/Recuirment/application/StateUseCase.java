package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.Country;
import com.hrms.Recuirment.domain.State;
import com.hrms.Recuirment.dto.CountryResponse;
import com.hrms.Recuirment.dto.StateCreateReq;
import com.hrms.Recuirment.dto.StateResponse;
import com.hrms.Recuirment.infrastructure.CountryRepository;
import com.hrms.Recuirment.infrastructure.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StateUseCase {
    @Autowired
    private final StateRepository stateRepository;
    @Autowired
    private final CountryRepository countryRepository;
    public String saveState(StateCreateReq createReq){
        Country country= countryRepository.findById(createReq.getCountryId())
                                          .orElseThrow(()->new RuntimeException("country not found"));
        State createReqObj=new State();
        createReqObj.setStateCode(createReq.getStateCode());
        createReqObj.setStateName(createReq.getStateName());
        createReqObj.setStatus("Y");
        createReqObj.setCountry(country);
        State save=stateRepository.save(createReqObj);
        return "Save successfully";
    }
    public List<StateResponse> getAllState(){
        List<StateResponse> stateList=stateRepository.findAll().stream().map(this::todto).toList();
        return stateList;
    }

    public StateResponse updateById(Long id, StateCreateReq updatedData) {
        State existingData=stateRepository.findById(id)
                .orElseThrow(()->new RuntimeException("state not found by id"+id));
        if(existingData!=null){
            existingData.setStateCode(updatedData.getStateCode());
            existingData.setStateName(updatedData.getStateName());

            //get country using country id;
            Country country=countryRepository.findById(id)
                    .orElseThrow(()->new RuntimeException("country not found with id"+updatedData.getCountryId()));
            if(country!=null){
                existingData.setCountry(country);
            }
            State savedData=stateRepository.save(existingData);
            return todto(savedData);
        }else {
            return null;
        }
    }
    public String updateStatusById(Long id) {
        Optional<State>optionalData=stateRepository.findById(id);
        if(optionalData.isPresent()){
            State existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            stateRepository.save(existingData);
            return "update state successfully";
        }else {
            return "state not found";
        }
    }
    private StateResponse todto(State state) {
        StateResponse res=new StateResponse();
        res.setId(state.getId());
        res.setStateCode(state.getStateCode());
        res.setStateName(state.getStateName());
        res.setStatus(state.getStatus());
        res.setCountryId(state.getCountry().getId());
        res.setLastChangeAt(state.getCreatedAt());
        res.setLastChangeBy(state.getCreatedBy());
        return res;
    }
    public State toentity(StateResponse dto){
        State res=new State();
        res.setId(dto.getId());
        res.setStateCode(dto.getStateCode());
        res.setStateName(dto.getStateName());
        res.setStatus(dto.getStatus());

        Country country=new Country();
        country.setId(dto.getCountryId());
        res.setCountry(country);
        return res;
    }

}
