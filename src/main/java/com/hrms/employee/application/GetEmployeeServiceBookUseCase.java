package com.hrms.employee.application;

import com.hrms.document.infrastructure.EmployeeDocumentRepository;
import com.hrms.employee.domain.*;
import com.hrms.employee.dto.*;
import com.hrms.employee.infrastructure.*;
import com.hrms.leave.infrastructure.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetEmployeeServiceBookUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeServiceHistoryRepository historyRepo;
    private final EmployeeQualificationRepository qualificationRepo;
    private final EmployeeSkillRepository skillRepo;
    private final EmployeeTrainingRepository trainingRepo;

    private final EmployeeDocumentRepository documentRepo;
    private final LeaveRepository leaveRepo;

    public EmployeeServiceBookResponse execute(Long employeeId) {

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeServiceBookResponse res = new EmployeeServiceBookResponse();

        // 🔹 Basic Info
        res.setEmployeeId(emp.getId());
        res.setEmployeeCode(emp.getEmployeeCode());
        res.setName(emp.getFirstName() + " " + emp.getLastName());
        res.setRole(emp.getRole().getName());
        res.setDepartment(emp.getDepartment() != null ? emp.getDepartment().getName() : null);
        res.setBranch(emp.getBranch() != null ? emp.getBranch().getName() : null);

        // 🔥 Service History
        res.setServiceHistory(
                historyRepo.findByEmployee_Id(employeeId)
                        .stream()
                        .map(h -> {
                            ServiceHistoryDto d = new ServiceHistoryDto();
                            d.setDesignation(
                                    h.getDesignation() != null ? h.getDesignation().getName() : null
                            );
                            d.setDepartment(h.getDepartmentNameSnapshot());
                            d.setFromDate(h.getFromDate());
                            d.setToDate(h.getToDate());
                            d.setOrderReference(h.getOrderReference());
                            return d;
                        }).collect(Collectors.toList())
        );

        // 📄 Documents
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

        // 🌴 Leaves
        res.setLeaves(
                leaveRepo.findByEmployeeIdAndIsDeletedFalse(employeeId)
                        .stream()
                        .map(l -> {
                            LeaveDto d = new LeaveDto();
                            d.setLeaveType(l.getLeaveType().getName());
                            d.setStartDate(l.getStartDate());
                            d.setEndDate(l.getEndDate());
                            d.setDays(l.getTotalDays());
                            d.setStatus(l.getStatus());
                            return d;
                        }).collect(Collectors.toList())
        );

        // 🎓 Qualifications
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

        // 🧠 Skills
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

        // 📚 Trainings
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

        return res;
    }
}
