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
@RequestMapping("/api/pension-eligibility")
@RequiredArgsConstructor
public class PensionEligibilityController {

    private final CreatePensionEligibilityUseCase createUseCase;
    private final UpdatePensionEligibilityUseCase updateUseCase;
    private final ChangePensionEligibilityStatusUseCase changeStatusUseCase;
    private final GetPensionEligibilityUseCase getUseCase;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> create(
            @RequestBody CreatePensionEligibilityRequest request) {
        return ResponseEntity.ok(createUseCase.execute(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<DefaultResponse>> update(
            @RequestBody UpdatePensionEligibilityRequest request) {
        return ResponseEntity.ok(updateUseCase.execute(request));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<DefaultResponse>> changeStatus(
            @PathVariable Long id) {
        return ResponseEntity.ok(changeStatusUseCase.execute(id));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<PensionEligibilityResponse>>> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return ResponseEntity.ok(getUseCase.execute(flag));
    }
}