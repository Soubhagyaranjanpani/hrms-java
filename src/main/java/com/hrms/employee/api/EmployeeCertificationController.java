package com.hrms.employee.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.application.EmployeeCertificationUseCase;
import com.hrms.employee.application.GetAllEmployeeCertificationUseCase;
import com.hrms.employee.domain.EmployeeCertification;
import com.hrms.employee.dto.EmployeeCertificationRequest;
import com.hrms.employee.dto.EmployeeCertificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-certification")
@RequiredArgsConstructor
public class EmployeeCertificationController {
    private  final EmployeeCertificationUseCase employeeCertificationUseCase;
    private final GetAllEmployeeCertificationUseCase getAllEmployeeCertificationUseCase;
    @PostMapping
    public ApiResponse<String> create(@RequestBody EmployeeCertificationRequest request) {
        return ResponseUtils.createSuccessResponse(employeeCertificationUseCase.create(request), new TypeReference<>() {}
        );
    }

    @GetMapping("/getAll")
    public ApiResponse<List<EmployeeCertificationResponse>> getAll(){
        return getAllEmployeeCertificationUseCase.getAll();

    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeCertification> getById(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                getAllEmployeeCertificationUseCase.getById(id),
                new TypeReference<>() {}
        );
    }






}
