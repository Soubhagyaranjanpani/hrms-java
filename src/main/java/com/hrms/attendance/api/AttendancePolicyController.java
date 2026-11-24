package com.hrms.attendance.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.attendance.application.AttendancePolicyService;
import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies/attendance")
@RequiredArgsConstructor
public class AttendancePolicyController {

    private final AttendancePolicyService service;

    @GetMapping
    public ApiResponse<List<AttendancePolicy>> getAll() {
        return ResponseUtils.createSuccessResponse(
                service.getAll(),
                new TypeReference<>() {}
        );
    }

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

    @GetMapping("/{id}")
    public ApiResponse<AttendancePolicy> getById(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                service.getById(id),
                new TypeReference<>() {}
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<AttendancePolicy> update(@PathVariable Long id, @RequestBody AttendancePolicy req) {
        return ResponseUtils.createSuccessResponse(
                service.update(id, req),
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

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseUtils.createSuccessResponse(
                "Deleted successfully",
                new TypeReference<>() {}
        );
    }
}
