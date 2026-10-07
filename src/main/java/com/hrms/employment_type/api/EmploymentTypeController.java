package com.hrms.employment_type.api;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.employment_type.application.*;   // Fixed import
import com.hrms.employment_type.dto.*;          // Fixed import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employment-types")
@RequiredArgsConstructor
public class EmploymentTypeController {

    private final CreateEmploymentTypeUseCase createEmploymentTypeUseCase;
    private final UpdateEmploymentTypeUseCase updateEmploymentTypeUseCase;
    private final ChangeEmploymentTypeStatusUseCase changeEmploymentTypeStatusUseCase;
    private final GetEmploymentTypeUseCase getEmploymentTypeUseCase;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> create(
            @RequestBody EmploymentTypeCreateReq request) {
        return ResponseEntity.ok(createEmploymentTypeUseCase.execute(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<DefaultResponse>> update(
            @RequestBody EmploymentTypeUpdateReq request) {
        return ResponseEntity.ok(updateEmploymentTypeUseCase.execute(request));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<DefaultResponse>> changeStatus(
            @PathVariable Long id) {
        return ResponseEntity.ok(changeEmploymentTypeStatusUseCase.execute(id));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<EmploymentTypeResponse>>> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return ResponseEntity.ok(getEmploymentTypeUseCase.execute(flag));
    }
}