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
@RequestMapping("/api/deputation-types")
@RequiredArgsConstructor
public class DeputationTypeController {

    private final CreateDeputationTypeUseCase createDeputationTypeUseCase;
    private final UpdateDeputationTypeUseCase updateDeputationTypeUseCase;
    private final ChangeDeputationTypeStatusUseCase changeDeputationTypeStatusUseCase;
    private final GetDeputationTypeUseCase getDeputationTypeUseCase;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> create(
            @RequestBody DeputationTypeCreateReq request) {
        return ResponseEntity.ok(createDeputationTypeUseCase.execute(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<DefaultResponse>> update(
            @RequestBody DeputationTypeUpdateReq request) {
        return ResponseEntity.ok(updateDeputationTypeUseCase.execute(request));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<DefaultResponse>> changeStatus(
            @PathVariable Long id) {
        return ResponseEntity.ok(changeDeputationTypeStatusUseCase.execute(id));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DeputationTypeResponse>>> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return ResponseEntity.ok(getDeputationTypeUseCase.execute(flag));
    }
}