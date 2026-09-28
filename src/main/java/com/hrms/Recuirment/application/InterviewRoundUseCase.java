package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.InterviewRound;
import com.hrms.Recuirment.dto.InterviewRoundReq;
import com.hrms.Recuirment.dto.InterviewRoundResponse;
import com.hrms.Recuirment.infrastructure.InterviewRoundRepository;
import com.hrms.common.utils.CurrentUser;
import com.hrms.master.domain.Department;
import com.hrms.master.infrastructure.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewRoundUseCase {
    @Autowired
    private final InterviewRoundRepository interviewRoundRepository;
    @Autowired
    private final DepartmentRepository departmentRepository;
    @Autowired
    private CurrentUser currentUser;

    public String saveInterviewRound(InterviewRoundReq createReq) {
        Department department=departmentRepository.findById(createReq.getDepartmentId())
                                           .orElseThrow(()->new RuntimeException("Department not found"));
        InterviewRound createObj=new InterviewRound();
        createObj.setRoundCode(createReq.getRoundCode());
        createObj.setRoundName(createReq.getRoundName());
        createObj.setRoundSequence(createReq.getRoundSequence());
        createObj.setMandatory(createReq.getMandatory());
        createObj.setMaximumScore(createReq.getMaximumScore());
        createObj.setPassingScore(createReq.getPassingScore());
        createObj.setStatus("y");
        createObj.setDepartment(department);
        createObj.setCreatedAt(LocalDateTime.now());
        createObj.setCreatedBy(currentUser.getEmployee().getFirstName());
        InterviewRound save=interviewRoundRepository.save(createObj);
        return "InterviewRound Saved successfully";
    }
    public List<InterviewRoundResponse> getAllInterviewRound() {
        List<InterviewRoundResponse>interviewRoundList=interviewRoundRepository.findAll().stream().map(this::todto).toList();
        return interviewRoundList;
    }
    public List<InterviewRoundResponse> getInterviewRoundByDepartmentId(Long id) {
        List<InterviewRoundResponse>interviewRound=interviewRoundRepository.findByDepartmentId(id).stream().map(this::todto).toList();
        return interviewRound;
    }

    public InterviewRoundResponse updateInterviewRoundById(Long id, InterviewRoundReq updatedData) {
        InterviewRound existingData=interviewRoundRepository.findById(id).orElseThrow(()->new RuntimeException("interview round not found"+id));
        if(existingData!=null){
            //update basic Fields
            existingData.setRoundCode(updatedData.getRoundCode());
            existingData.setRoundName(updatedData.getRoundName());
            existingData.setRoundSequence(updatedData.getRoundSequence());
            existingData.setMandatory(updatedData.getMandatory());
            existingData.setMaximumScore(updatedData.getMaximumScore());
            existingData.setPassingScore(updatedData.getPassingScore());
            existingData.setStatus(updatedData.getStatus());
            //get Department using departmentId
            Department department =
                    departmentRepository.findById(updatedData.getDepartmentId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Department not found: "
                                                    + updatedData.getDepartmentId()));
            if(department!=null){
                existingData.setDepartment(department);
            }
            //save to entity
            InterviewRound savedData=interviewRoundRepository.save(existingData);
            return todto(savedData);
        }else{
            return null;
        }
    }
    public String updateInterviewRoundStatus(Long id) {
        Optional<InterviewRound>optionalData=interviewRoundRepository.findById(id);
        if(optionalData.isPresent()){
            InterviewRound existingData=optionalData.get();
            if("y".equals(existingData.getStatus())){
                existingData.setStatus("n");
            }else {
                existingData.setStatus("y");
            }
            interviewRoundRepository.save(existingData);
            return "update interviewRound successfully";
        }else{
            return "InterviewRound not found";
        }

    }

    private InterviewRoundResponse todto(InterviewRound interviewRound) {
        InterviewRoundResponse res=new InterviewRoundResponse();
        res.setId(interviewRound.getId());
        res.setRoundCode(interviewRound.getRoundCode());
        res.setRoundName(interviewRound.getRoundName());
        res.setRoundSequence(interviewRound.getRoundSequence());
        res.setMandatory(interviewRound.getMandatory());
        res.setMaximumScore(interviewRound.getMaximumScore());
        res.setPassingScore(interviewRound.getPassingScore());
        res.setDepartmentId(interviewRound.getDepartment().getId());
        res.setStatus(interviewRound.getStatus());
        res.setLastChangeAt(LocalDateTime.now());
        res.setLastChangeBy(currentUser.getEmployee().getFirstName());
        return res;
    }
    private InterviewRound toentity(InterviewRoundResponse dto){
        InterviewRound response=new InterviewRound();
        response.setId(dto.getId());
        response.setRoundCode(dto.getRoundCode());
        response.setRoundName(dto.getRoundName());
        response.setRoundSequence(dto.getRoundSequence());
        response.setMandatory(dto.getMandatory());
        response.setMaximumScore(dto.getMaximumScore());
        response.setPassingScore(dto.getPassingScore());
        response.setStatus(dto.getStatus());

        Department department=new Department();
        department.setId(dto.getDepartmentId());
        response.setDepartment(department);
        return response;
    }

}
