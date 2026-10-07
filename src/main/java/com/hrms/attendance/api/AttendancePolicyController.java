package com.hrms.attendance.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.attendance.application.AttendancePolicyService;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies/attendance")
@RequiredArgsConstructor
public class AttendancePolicyController {

    private final AttendancePolicyService service;

    @PostMapping
    public ApiResponse<AttendancePolicy> create(@RequestBody AttendancePolicy req) {
        return ResponseUtils.createSuccessResponse(
                service.save(req),
                new TypeReference<>() {}
        );
    }

    @GetMapping("/active")
    public ApiResponse<AttendancePolicy> getActive() {
        return ResponseUtils.createSuccessResponse(
                service.getActive(),
                new TypeReference<>() {}
        );
    }

    @PutMapping("/activate/{id}")
    public ApiResponse<String> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseUtils.createSuccessResponse(
                "Activated",
                new TypeReference<>() {}
        );
    }
}
