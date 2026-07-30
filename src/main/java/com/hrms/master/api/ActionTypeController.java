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
@RequestMapping("/api/action-types")
@RequiredArgsConstructor
public class ActionTypeController {

    private final CreateActionTypeUseCase createUseCase;
    private final UpdateActionTypeUseCase updateUseCase;
    private final ChangeActionTypeStatusUseCase changeStatusUseCase;
    private final GetActionTypeUseCase getUseCase;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> create(
            @RequestBody ActionTypeCreateReq request) {
        return ResponseEntity.ok(createUseCase.execute(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<DefaultResponse>> update(
            @RequestBody ActionTypeUpdateReq request) {
        return ResponseEntity.ok(updateUseCase.execute(request));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<DefaultResponse>> changeStatus(
            @PathVariable Long id) {
        return ResponseEntity.ok(changeStatusUseCase.execute(id));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ActionTypeResponse>>> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return ResponseEntity.ok(getUseCase.execute(flag));
    }
}