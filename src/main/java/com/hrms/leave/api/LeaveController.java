package com.hrms.leave.api;

import com.hrms.leave.application.ApplyLeaveUseCase;
import com.hrms.leave.dto.LeaveApplyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final ApplyLeaveUseCase applyLeaveUseCase;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveApplyRequest request) {
        return ResponseEntity.ok(applyLeaveUseCase.execute(request));
    }
}
