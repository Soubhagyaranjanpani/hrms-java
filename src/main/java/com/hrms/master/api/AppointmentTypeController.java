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
@RequestMapping("/api/appointment-types")
@RequiredArgsConstructor
public class AppointmentTypeController {

    private final CreateAppointmentTypeUseCase createAppointmentTypeUseCase;
    private final UpdateAppointmentTypeUseCase updateAppointmentTypeUseCase;
    private final ChangeAppointmentTypeStatusUseCase changeAppointmentTypeStatusUseCase;
    private final GetAppointmentTypeUseCase getAppointmentTypeUseCase;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> create(
            @RequestBody AppointmentTypeCreateReq request) {
        return ResponseEntity.ok(createAppointmentTypeUseCase.execute(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<DefaultResponse>> update(
            @RequestBody AppointmentTypeUpdateReq request) {
        return ResponseEntity.ok(updateAppointmentTypeUseCase.execute(request));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<DefaultResponse>> changeStatus(
            @PathVariable Long id) {
        return ResponseEntity.ok(changeAppointmentTypeStatusUseCase.execute(id));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<AppointmentTypeResponse>>> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return ResponseEntity.ok(getAppointmentTypeUseCase.execute(flag));
    }
}