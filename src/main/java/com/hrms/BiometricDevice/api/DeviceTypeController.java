package com.hrms.BiometricDevice.api;

import com.hrms.BiometricDevice.application.StatusDeviceTypeUseCase;
import com.hrms.BiometricDevice.application.CreateDeviceTypeUseCase;
import com.hrms.BiometricDevice.application.GetAllDeviceTypeUseCase;
import com.hrms.BiometricDevice.application.UpdateDeviceTypeUseCase;
import com.hrms.BiometricDevice.application.GetAllDeviceTypeUseCase;
import com.hrms.BiometricDevice.application.StatusDeviceTypeUseCase;
import com.hrms.BiometricDevice.application.UpdateDeviceTypeUseCase;
import com.hrms.BiometricDevice.domain.DeviceType;
import com.hrms.BiometricDevice.dto.DeviceTypeRequest;
import com.hrms.BiometricDevice.dto.DeviceTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-types")
@RequiredArgsConstructor
public class DeviceTypeController {

    private final CreateDeviceTypeUseCase createDeviceTypeUseCase;

    private final GetAllDeviceTypeUseCase getAllDeviceTypeUseCase;

    private final UpdateDeviceTypeUseCase updateDeviceTypeUseCase;

    private final StatusDeviceTypeUseCase
           statusDeviceTypeUseCase;


    // CREATE
    @PostMapping
    public DeviceType create(
            @RequestBody DeviceTypeRequest request) {

        return createDeviceTypeUseCase.execute(request);
    }


    // GET ALL
    @GetMapping
    public List<DeviceTypeResponse> getAll(
            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            Integer flag) {

        return getAllDeviceTypeUseCase.execute(flag);
    }


     //UPDATE
    @PutMapping("/{id}")
    public DeviceType update(
            @PathVariable Long id,
            @RequestBody DeviceTypeRequest request) {

        return updateDeviceTypeUseCase.execute(
                id,
                request
        );
    }


    // CHANGE STATUS
    @PutMapping("/{id}/status")
    public DeviceType changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean status) {

        return statusDeviceTypeUseCase.execute(
                id,
                status
        );
    }
}
