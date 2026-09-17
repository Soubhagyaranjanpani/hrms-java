package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.*;
import com.hrms.Recuirment.dto.JobLocationReq;
import com.hrms.Recuirment.dto.JobLocationResponse;
import com.hrms.Recuirment.infrastructure.*;
import com.hrms.common.utils.CurrentUser;
import com.hrms.master.domain.Branch;
import com.hrms.master.infrastructure.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobLocationUseCase {
    @Autowired
    private final JobLocationRepository jobLocationRepository;
    @Autowired
    private final BranchRepository branchRepository;
    @Autowired
    private final CountryRepository countryRepository;
    @Autowired
    private final StateRepository stateRepository;
    @Autowired
    private final CityRepository cityRepository;
    @Autowired
    private final WorkModeRepository workModeRepository;
    @Autowired
    private final CurrentUser currentUser;

    public String saveLocation(JobLocationReq locationReq) {
        Branch branch=branchRepository.findById(locationReq.getBranchId())
                                      .orElseThrow(()->new RuntimeException("Branch not found"));
        Country country=countryRepository.findById(locationReq.getCountryId())
                                      .orElseThrow(()->new RuntimeException("Country not found"));
        State state =stateRepository.findById(locationReq.getStateId())
                                      .orElseThrow(()->new RuntimeException("State not found"));
        City city =cityRepository.findById(locationReq.getCityId())
                                      .orElseThrow(()->new RuntimeException("City not found"));
        WorkMode workMode =workModeRepository.findById(locationReq.getWorkModeId())
                                      .orElseThrow(()->new RuntimeException("WorkMode not found"));
        JobLocation createObj=new JobLocation();
        createObj.setLocationCode(locationReq.getLocationCode());
        createObj.setLocationName(locationReq.getLocationName());
        createObj.setPinCode(locationReq.getPinCode());
        createObj.setDescription(locationReq.getDescription());
        createObj.setStatus("Y");
        createObj.setBranch(branch);
        createObj.setCountry(country);
        createObj.setState(state);
        createObj.setCity(city);
        createObj.setWorkMode(workMode);
        createObj.setCreatedAt(LocalDateTime.now());
        createObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        JobLocation save=jobLocationRepository.save(createObj);
        return "Save location successfully";
    }
    public List<JobLocationResponse> getJobLocation() {
        List<JobLocationResponse>jobLocationList=jobLocationRepository.findAll()
                                                                      .stream()
                                                                      .map(this::todto)
                                                                      .toList();
        return jobLocationList;
    }
    public JobLocationResponse updateById(Long id, JobLocationReq updatedData) {
        JobLocation existingData=jobLocationRepository.findById(id).orElseThrow(()->new RuntimeException("JobLocation not found with id"+id));
        if(existingData!=null){
            //update basic fields
            existingData.setLocationCode(updatedData.getLocationCode());
            existingData.setLocationName(updatedData.getLocationName());
            existingData.setPinCode(updatedData.getPinCode());
            existingData.setDescription(updatedData.getDescription());
            //get branch using branchId
            Branch branch =branchRepository.findById(updatedData.getBranchId())
                                           .orElseThrow(()->new RuntimeException("Branch not found with id"+updatedData.getBranchId()));
            if(branch!=null){
                existingData.setBranch(branch);
            }
            //get Country using countryId
            Country country =countryRepository.findById(updatedData.getCountryId())
                                         .orElseThrow(()->new RuntimeException("Country not found with id"+updatedData.getCountryId()));
            if(country!=null){
                existingData.setCountry(country);
            }
            //get State using stateId
            State state=stateRepository.findById(updatedData.getStateId())
                                         .orElseThrow(()->new RuntimeException("State not found with id"+updatedData.getStateId()));
            if (state!=null){
                existingData.setState(state);
            }
            //get City using cityId
            City city=cityRepository.findById(updatedData.getCityId())
                                 .orElseThrow(()->new RuntimeException("City not found with"+updatedData.getCityId()));
            if (city!=null){
                existingData.setCity(city);
            }
            //get WorkMode using workModeId
            WorkMode workMode=workModeRepository.findById(updatedData.getWorkModeId())
                                         .orElseThrow(()->new RuntimeException("workmode not found with id"+updatedData.getWorkModeId()));
            if(workMode!=null){
                existingData.setCity(city);
            }
            //save updated entity
            JobLocation savedData=jobLocationRepository.save(existingData);
            return todto(savedData);
        }else {
            return null;
        }
    }

    public String updateStatusById(Long id) {
        Optional<JobLocation> optionalData=jobLocationRepository.findById(id);
        if(optionalData.isPresent()){
            JobLocation existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            jobLocationRepository.save(existingData);
            return "updated jobLocation successfully";
        }else {
            return "JobLocation not found";
        }
    }

    private JobLocationResponse todto(JobLocation jobLocation) {
        JobLocationResponse res=new JobLocationResponse();
        res.setId(jobLocation.getId());
        res.setLocationCode(jobLocation.getLocationCode());
        res.setPinCode(jobLocation.getPinCode());
        res.setDescription(jobLocation.getDescription());
        res.setStatus(jobLocation.getStatus());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        res.setBranchId(jobLocation.getBranch().getId());
        res.setCountryId(jobLocation.getCountry().getId());
        res.setStateId(jobLocation.getState().getId());
        res.setCityId(jobLocation.getCity().getId());
        res.setWorkModeId(jobLocation.getWorkMode().getId());
        return res;
    }
    private JobLocation toentity(JobLocationResponse dto){
        JobLocation res=new JobLocation();
        res.setId(dto.getId());
        res.setLocationCode(dto.getLocationCode());
        res.setLocationName(dto.getLocationName());
        res.setPinCode(dto.getPinCode());
        res.setDescription(dto.getDescription());
        res.setStatus(dto.getStatus());

        Branch branch =new Branch();
        branch.setId(dto.getBranchId());
        res.setBranch(branch);

        Country country=new Country();
        country.setId(dto.getCountryId());
        res.setCountry(country);

        State state =new State();
        state.setId(dto.getStateId());
        res.setState(state);

        City city =new City();
        city.setId(dto.getCityId());
        res.setCity(city);

        WorkMode workMode=new WorkMode();
        workMode.setId(dto.getCityId());
        res.setWorkMode(workMode);
        return res;
    }

}

