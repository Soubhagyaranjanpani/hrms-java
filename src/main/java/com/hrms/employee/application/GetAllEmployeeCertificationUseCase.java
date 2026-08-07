package com.hrms.employee.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.EmployeeCertification;
import com.hrms.employee.dto.EmployeeCertificationResponse;
import com.hrms.employee.infrastructure.EmployeeCertificationRepository;
import com.hrms.master.domain.Designation;
import com.hrms.master.dto.DepartmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllEmployeeCertificationUseCase {
    private final EmployeeCertificationRepository employeeCertificationRepository;

    public ApiResponse<List<EmployeeCertificationResponse>> getAll() {
        List<EmployeeCertification> list;
        list = employeeCertificationRepository.findAll();

        List<EmployeeCertificationResponse> responses = list.stream().map(this::mapToResponse).toList();
        return ResponseUtils.createSuccessResponse(responses, new TypeReference<List<EmployeeCertificationResponse>>() {
        });


}

    public EmployeeCertification getById(Long id) {
        return employeeCertificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeCertification not found with id: " + id));
    }

private EmployeeCertificationResponse mapToResponse(EmployeeCertification entity){
        EmployeeCertificationResponse response = new EmployeeCertificationResponse();
        response.setId(entity.getCertificateId());
        response.setCertificateNumber(entity.getCertificateNumber());
        response.setCertificateName(entity.getCertificateName());
        response.setExpiryReminderDays(entity.getExpiryReminderDays());
        response.setNotes(entity.getNotes());
        response.setIssueAuthority(entity.getIssueAuthority());
        response.setIssueDate(entity.getIssueDate());
        response.setExpiryDate(entity.getExpiryDate());
        response.setEmployeeId(entity.getEmployee().getId());
        response.setEmployeeName(entity.getEmployee().getFullName());

        return response;

}





}
