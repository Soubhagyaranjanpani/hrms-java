package com.hrms.attendance.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.attendance.application.ShiftPolicyService;
import com.hrms.attendance.domain.ShiftMaster;
import com.hrms.attendance.dto.ShiftPolicySettingsDTO;
import com.hrms.attendance.dto.ShiftResponse;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftPolicyService shiftPolicyService;

    /**
     * Get all configured shift master categories.
     * Accessible to all authenticated users (Employees, HR, Admin).
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<ShiftResponse>> getAllShifts() {
        return ResponseUtils.createSuccessResponse(
                shiftPolicyService.getAllShifts(),
                new TypeReference<>() {}
        );
    }

    /**
     * Create or save a shift category into PostgreSQL shift_master.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ShiftResponse> createShift(@RequestBody ShiftMaster req) {
        return ResponseUtils.createSuccessResponse(
                shiftPolicyService.saveShift(req),
                new TypeReference<>() {}
        );
    }

    /**
     * Update an existing shift category in PostgreSQL shift_master.
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ShiftResponse> updateShift(@PathVariable Long id, @RequestBody ShiftMaster req) {
        return ResponseUtils.createSuccessResponse(
                shiftPolicyService.updateShift(id, req),
                new TypeReference<>() {}
        );
    }

    /**
     * Delete/deactivate a shift category from PostgreSQL shift_master.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> deleteShift(@PathVariable Long id) {
        shiftPolicyService.deleteShift(id);
        return ResponseUtils.createSuccessResponse(
                "Shift deleted successfully",
                new TypeReference<>() {}
        );
    }

    /**
     * Get a specific shift category by its code (e.g. GS, MS, DS, NS, S39).
     */
    @GetMapping("/{code}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ShiftResponse> getShiftByCode(@PathVariable String code) {
        return ResponseUtils.createSuccessResponse(
                shiftPolicyService.getShiftByCode(code),
                new TypeReference<>() {}
        );
    }

    /**
     * Get current shift policy settings (check-in window, checkout rules, grace mins).
     */
    @GetMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ShiftPolicySettingsDTO> getShiftSettings() {
        return ResponseUtils.createSuccessResponse(
                shiftPolicyService.getShiftSettings(),
                new TypeReference<>() {}
        );
    }

    /**
     * Update shift policy window rules and persist to PostgreSQL attendance_policy.
     * Strictly restricted to HR and Admin roles.
     */
    @PostMapping("/settings")
    @PreAuthorize("hasRole('HR') or hasRole('Admin') or hasRole('ADMIN')")
    public ApiResponse<ShiftPolicySettingsDTO> updateShiftSettings(@RequestBody ShiftPolicySettingsDTO dto) {
        return ResponseUtils.createSuccessResponse(
                shiftPolicyService.updateShiftSettings(dto),
                new TypeReference<>() {}
        );
    }

    /**
     * Get the logged-in employee's shift for today.
     * Accessible to the authenticated employee (role-isolated self-service).
     */
    @GetMapping("/my-shift")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ShiftResponse> getMyShift(
            @RequestParam(required = false, defaultValue = "GS") String shiftCode,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseUtils.createSuccessResponse(
                shiftPolicyService.getMyShift(username, shiftCode),
                new TypeReference<>() {}
        );
    }
}
