package com.hrms.Recuirment.application;

import com.hrms.Recuirment.domain.InterviewPanel;
import com.hrms.Recuirment.dto.InterviewPanelCreateReq;
import com.hrms.Recuirment.dto.InterviewPanelResponse;
import com.hrms.Recuirment.infrastructure.InterviewPanelRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Department;
import com.hrms.master.infrastructure.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewPanelUseCase {
    @Autowired
    private final InterviewPanelRepository interviewPanelRepository;
    @Autowired
    private final DepartmentRepository departmentRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    public String createPanel(InterviewPanelCreateReq createReq) {

        Department department = departmentRepository.findById(createReq.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("department not found"));
        Employee primary_interviewer = employeeRepository.findById(createReq.getPrimaryInterviewerId())
                .orElseThrow(() -> new RuntimeException("primary interviewer not found"));
        Employee secondary_interviewer = employeeRepository.findById(createReq.getSecondaryInterviewerId())
                .orElseThrow(() -> new RuntimeException("secondary interviewer not found"));
        List<Employee> panel_members = employeeRepository.findAllById(createReq.getPanelMembersIds());

        InterviewPanel createReqObj = new InterviewPanel();
        createReqObj.setPanelCode(createReq.getPanelCode());
        createReqObj.setPanelName(createReq.getPanelName());
        createReqObj.setStatus("Y");
        createReqObj.setDepartment(department);
        createReqObj.setPrimaryInterviewer(primary_interviewer);
        createReqObj.setSecondaryInterviewer(secondary_interviewer);
        createReqObj.setPanelMembers(panel_members);
        InterviewPanel save = interviewPanelRepository.save(createReqObj);
        return "create successfully";
    }
        public List<InterviewPanelResponse> getInterviewPanel() {
            List<InterviewPanelResponse> interviewPanelList = interviewPanelRepository.findAll()
                    .stream()
                    .map(this::todto)
                    .toList();
            return interviewPanelList;
        }
        public String updateStatusById(Long id){
              Optional<InterviewPanel> optionalData=interviewPanelRepository.findById(id);
                if(optionalData.isPresent()){
                    InterviewPanel existingData=optionalData.get();
                    if("y".equals(existingData.getStatus())){
                        existingData.setStatus("n");
                    }else {
                        existingData.setStatus("y");
                    }
                    interviewPanelRepository.save(existingData);

                    return "update status successfully";
                }else {
                    return "interview panel not found";
                }
            }

    public InterviewPanelResponse updateById(Long id, InterviewPanelCreateReq updatedData) {
        // Find existing panel
        InterviewPanel existingData = interviewPanelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview Panel not found with id: " + id));
        if(existingData!=null) {
            // Update basic fields
            existingData.setPanelCode(updatedData.getPanelCode());
            existingData.setPanelName(updatedData.getPanelName());

            // Get Department using departmentId
            Department department = departmentRepository.findById(updatedData.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + updatedData.getDepartmentId()));
            if (department != null) {
                existingData.setDepartment(department);
            }
            // Get Primary Interviewer using ID
            Employee primaryInterviewer = employeeRepository.findById(updatedData.getPrimaryInterviewerId())
                    .orElseThrow(() -> new RuntimeException("Primary interviewer not found with id: " + updatedData.getPrimaryInterviewerId()));
            existingData.setPrimaryInterviewer(primaryInterviewer);
            // Get Secondary Interviewer using ID
            if (updatedData.getSecondaryInterviewerId() != null) {
                Employee secondaryInterviewer = employeeRepository.findById(updatedData.getSecondaryInterviewerId())
                        .orElseThrow(() -> new RuntimeException("Secondary interviewer not found with id: " + updatedData.getSecondaryInterviewerId()));
                existingData.setSecondaryInterviewer(secondaryInterviewer);
            } else {
                existingData.setSecondaryInterviewer(null);
            }
            // Update panel members
            if (updatedData.getPanelMembersIds() != null) {
                List<Employee> panelMembers = employeeRepository
                        .findAllById(updatedData.getPanelMembersIds());
                if (panelMembers.size() != updatedData.getPanelMembersIds().size()) {
                    throw new RuntimeException("One or more panel members not found");
                }
                existingData.setPanelMembers(panelMembers);
            } else {
                existingData.setPanelMembers(new ArrayList<>());
            }
            // Save updated entity
            InterviewPanel savedData = interviewPanelRepository.save(existingData);
            // Convert entity to response DTO
            return todto(savedData);
        }else{
            return null;
        }
    }
            private InterviewPanelResponse todto(InterviewPanel interviewPanel) {

            InterviewPanelResponse res = new InterviewPanelResponse();

            res.setId(interviewPanel.getId());
            res.setPanelCode(interviewPanel.getPanelCode());
            res.setPanelName(interviewPanel.getPanelName());
            res.setStatus(interviewPanel.getStatus());
            res.setDepartmentId(interviewPanel.getDepartment().getId());
            res.setPrimaryInterviewerId(interviewPanel.getPrimaryInterviewer().getId());
            res.setLastChangeAt(interviewPanel.getCreatedAt());
            res.setLastChangeBy(interviewPanel.getCreatedBy());
            res.setSecondaryInterviewerId(
                    interviewPanel.getSecondaryInterviewer() != null
                            ? interviewPanel.getSecondaryInterviewer().getId() : null);
            res.setPanelMembersIds(
                    interviewPanel.getPanelMembers().stream().map(Employee::getId).toList()
            );
            return res;
        }

        public InterviewPanel toentity(InterviewPanelResponse dto) {
            InterviewPanel res = new InterviewPanel();
            res.setId(dto.getId());
            res.setPanelCode(dto.getPanelCode());
            res.setPanelName(dto.getPanelName());
            res.setStatus(dto.getStatus());

            Department department = new Department();
            department.setId(dto.getDepartmentId());
            res.setDepartment(department);

            Employee primaryInterviewer = new Employee();
            primaryInterviewer.setId(dto.getPrimaryInterviewerId());
            res.setPrimaryInterviewer(primaryInterviewer);

            if (dto.getSecondaryInterviewerId() != null) {
                Employee secondaryInterviewer = new Employee();
                secondaryInterviewer.setId(dto.getSecondaryInterviewerId());
                res.setSecondaryInterviewer(secondaryInterviewer);
            }
            List<Employee> panelMembers = dto.getPanelMembersIds().stream().map(id -> {
                        Employee employee = new Employee();
                        employee.setId(id);
                        return employee;
                    }).toList();
            res.setPanelMembers(panelMembers);
            return res;
        }

}



