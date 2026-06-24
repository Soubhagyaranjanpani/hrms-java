package com.hrms.employee.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.document.infrastructure.EmployeeDocumentRepository;
import com.hrms.employee.domain.*;
import com.hrms.employee.dto.*;
import com.hrms.employee.infrastructure.*;
import com.hrms.leave.domain.LeaveBalance;
import com.hrms.leave.infrastructure.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetEmployeeFullProfileUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeQualificationRepository qualificationRepo;
    private final EmployeeSkillRepository skillRepo;
    private final EmployeeTrainingRepository trainingRepo;
    private final EmployeeDocumentRepository documentRepo;
    private final LeaveBalanceRepository balanceRepo;

    public EmployeeFullProfileResponse execute(Long employeeId) {

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeFullProfileResponse res = new EmployeeFullProfileResponse();

        // Basic
        res.setId(emp.getId());
        res.setEmployeeCode(emp.getEmployeeCode());
        res.setName(emp.getFirstName() + " " + emp.getLastName());
        res.setEmail(emp.getEmail());
        res.setPhone(emp.getPhone());

        res.setRole(emp.getRole().getName());
        res.setDepartment(emp.getDepartment() != null ? emp.getDepartment().getName() : null);
        res.setBranch(emp.getBranch() != null ? emp.getBranch().getName() : null);

        res.setManagerName(emp.getManager() != null ? emp.getManager().getFirstName() : null);
        res.setJoiningDate(emp.getJoiningDate());

        // Qualifications
        res.setQualifications(
                qualificationRepo.findByEmployee_Id(employeeId)
                        .stream()
                        .map(q -> {
                            QualificationDto d = new QualificationDto();
                            d.setDegree(q.getDegree());
                            d.setInstitution(q.getInstitution());
                            d.setYear(q.getYear());
                            return d;
                        }).collect(Collectors.toList())
        );

        // Skills
        res.setSkills(
                skillRepo.findByEmployee_Id(employeeId)
                        .stream()
                        .map(s -> {
                            SkillDto d = new SkillDto();
                            d.setSkillName(s.getSkillName());
                            d.setProficiency(s.getProficiency());
                            return d;
                        }).collect(Collectors.toList())
        );

        // Trainings
        res.setTrainings(
                trainingRepo.findByEmployee_Id(employeeId)
                        .stream()
                        .map(t -> {
                            TrainingDto d = new TrainingDto();
                            d.setTrainingName(t.getTrainingName());
                            d.setCompletedOn(t.getCompletedOn());
                            return d;
                        }).collect(Collectors.toList())
        );

        // Documents
        res.setDocuments(
                documentRepo.findByEmployee_Id(employeeId)
                        .stream()
                        .map(doc -> {
                            DocumentDto d = new DocumentDto();
                            d.setFileName(doc.getFileName());
                            d.setCategory(doc.getCategory().name());
                            d.setDocumentType(doc.getDocumentType());
                            return d;
                        }).collect(Collectors.toList())
        );

        // Leave Summary (simple)
        LeaveBalance balance = balanceRepo.findTopByEmployee_IdOrderByYearDesc(employeeId)
                .orElse(null);

        if (balance != null) {
            res.setTotalLeaveTaken(balance.getUsed());
            res.setRemainingLeave(balance.getRemaining());
        }

        return res;
    }
    public ApiResponse<List<EmployeeSkill>> getEmployeeSkills(
            Long employeeId) {

        Employee employee = employeeRepo
                .findById(employeeId)
                .orElse(null);

        if (employee == null) {

            return ResponseUtils.createFailureResponse(
                    null,
                    new TypeReference<>() {},
                    "Employee not found",
                    404
            );
        }

        List<EmployeeSkill> skills =

        skillRepo.findByEmployee_Id(employeeId);

        return ResponseUtils.createSuccessResponse(
                skills,
                new TypeReference<>() {},
                "Employee skills fetched successfully"
        );
    }
    public ApiResponse<List<EmployeeQualification>> getEmployeeQualifications(
            Long employeeId) {

        Employee employee = employeeRepo
                .findById(employeeId)
                .orElse(null);

        if (employee == null) {

            return ResponseUtils.createFailureResponse(
                    null,
                    new TypeReference<>() {},
                    "Employee not found",
                    404
            );
        }

        List<EmployeeQualification> qualifications =
                qualificationRepo.findByEmployee_Id(employeeId);

        return ResponseUtils.createSuccessResponse(
                qualifications,
                new TypeReference<>() {},
                "Employee qualifications fetched successfully"
        );
    }
}
