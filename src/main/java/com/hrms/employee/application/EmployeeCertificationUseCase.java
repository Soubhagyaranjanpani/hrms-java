package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeCertification;
import com.hrms.employee.domain.EmployeeGrade;
import com.hrms.employee.dto.EmployeeCertificationRequest;
import com.hrms.employee.dto.EmployeeCertificationResponse;
import com.hrms.employee.infrastructure.EmployeeCertificationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeCertificationUseCase {

    private final EmployeeRepository employeeRep;
    private final EmployeeCertificationRepository employeeCertificationRepository;


    public String create(EmployeeCertificationRequest request) {

        Employee emp = employeeRep.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeCertification certification = new EmployeeCertification();
        certification.setCertificateName(request.getCertificateName());
        certification.setCertificateNumber(request.getCertificateNumber());
        certification.setIssueAuthority(request.getIssueAuthority());
        certification.setExpiryReminderDays(request.getExpiryReminderDays());
        certification.setIssueDate(request.getIssueDate());
        certification.setExpiryDate(request.getExpiryDate());
        certification.setNotes(request.getNotes());
        certification.setEmployee(emp);
        certification.setCreatedDate(LocalDate.now());
        certification.setUpdatedDate(LocalDate.now());
        employeeCertificationRepository.save(certification);


        return "certification add successfully";

    }




}
