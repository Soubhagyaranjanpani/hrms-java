package com.hrms.master.api;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.master.application.*;
import com.hrms.master.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final CreateDepartmentUseCase createDepartmentUseCase;
    private final UpdateDepartmentUseCase updateDepartmentUseCase;
    private final ChangeDepartmentStatusUseCase changeDepartmentStatusUseCase;
    private final GetDepartmentUseCase getDepartmentUseCase;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> create(
            @RequestBody DepartmentCreateReq request) {
        return ResponseEntity.ok(createDepartmentUseCase.execute(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<DefaultResponse>> update(
            @RequestBody DepartmentUpdateReq request) {
        return ResponseEntity.ok(updateDepartmentUseCase.execute(request));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<DefaultResponse>> changeStatus(
            @PathVariable Long id) {
        return ResponseEntity.ok(changeDepartmentStatusUseCase.execute(id));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return ResponseEntity.ok(getDepartmentUseCase.execute(flag));
    }
}