package com.hrms.leave.api;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.leave.application.*;
import com.hrms.leave.domain.LeaveType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-type")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final CreateLeaveTypeUseCase createUseCase;
    private final UpdateLeaveTypeUseCase updateUseCase;
    private final GetAllLeaveTypeUseCase getAllUseCase;
    private final ToggleLeaveTypeUseCase toggleUseCase;

    // 🔥 CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<String>> create(@RequestBody LeaveType request) {
        return ResponseEntity.ok(createUseCase.execute(request));
    }

    // 🔥 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> update(
            @PathVariable Long id,
            @RequestBody LeaveType request) {

        return ResponseEntity.ok(updateUseCase.execute(id, request));
    }

    // 🔥 GET ALL
    @GetMapping
    public ResponseEntity<List<LeaveType>> getAll() {
        return ResponseEntity.ok(getAllUseCase.execute());
    }

    // 🔥 ACTIVATE / DEACTIVATE
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> toggle(
            @PathVariable Long id,
            @RequestParam Boolean active) {

        return ResponseEntity.ok(toggleUseCase.execute(id, active));
    }
}
