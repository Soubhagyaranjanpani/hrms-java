package com.hrms.BiometricDevice.api;

import com.hrms.BiometricDevice.application.CreateBiometricDeviceUseCase;
import com.hrms.BiometricDevice.application.GetAllBiometricDeviceUseCase;
import com.hrms.BiometricDevice.application.GetBiometricDeviceByIdUseCase;
import com.hrms.BiometricDevice.application.StatusBiometricDeviceUseCase;
import com.hrms.BiometricDevice.application.UpdateBiometricDeviceUseCase;
import com.hrms.BiometricDevice.dto.BiometricDeviceRequest;
import com.hrms.BiometricDevice.dto.BiometricDeviceResponse;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biometric-devices")
@RequiredArgsConstructor
public class BiometricDeviceController {

    private final CreateBiometricDeviceUseCase createBiometricDeviceUseCase;

    private final UpdateBiometricDeviceUseCase updateBiometricDeviceUseCase;

    private final StatusBiometricDeviceUseCase statusBiometricDeviceUseCase;

    private final GetAllBiometricDeviceUseCase getAllBiometricDeviceUseCase;

    private final GetBiometricDeviceByIdUseCase getBiometricDeviceByIdUseCase;


    // CREATE

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> create(
            @RequestBody BiometricDeviceRequest request) {

        return ResponseEntity.ok(
                ResponseUtils.createSuccessResponse(
                        createBiometricDeviceUseCase.execute(request),
                        null
                )
        );
    }


    // UPDATE

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @RequestBody BiometricDeviceRequest request) {

        return ResponseEntity.ok(
                ResponseUtils.createSuccessResponse(
                        updateBiometricDeviceUseCase.execute(
                                id,
                                request
                        ),
                        null
                )
        );
    }


    // STATUS

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<?>> status(
            @PathVariable Long id,
            @RequestParam Boolean status) {

        return ResponseEntity.ok(
                ResponseUtils.createSuccessResponse(
                        statusBiometricDeviceUseCase.execute(
                                id,
                                status
                        ),
                        null
                )
        );
    }


    // GET ALL

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) Integer flag) {

        List<BiometricDeviceResponse> response =
                getAllBiometricDeviceUseCase.execute(flag);

        return ResponseEntity.ok(
                ResponseUtils.createSuccessResponse(
                        response,
                        null
                )
        );
    }


    // GET BY ID

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtils.createSuccessResponse(
                        getBiometricDeviceByIdUseCase.execute(id),
                        null
                )
        );
    }
}