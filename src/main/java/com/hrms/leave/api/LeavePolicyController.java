package com.hrms.leave.api;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.leave.application.CreateLeavePolicyUseCase;
import com.hrms.leave.application.UpdateLeavePolicyUseCase;
import com.hrms.leave.dto.LeavePolicyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-policy")
@RequiredArgsConstructor
public class LeavePolicyController {

    private final CreateLeavePolicyUseCase createUseCase;
    private final UpdateLeavePolicyUseCase updateUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> create(
            @RequestBody LeavePolicyRequest request) {

        return ResponseEntity.ok(createUseCase.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> update(
            @PathVariable Long id,
            @RequestBody LeavePolicyRequest request) {

        return ResponseEntity.ok(updateUseCase.execute(id, request));
    }
}
