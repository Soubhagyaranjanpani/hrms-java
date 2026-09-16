package com.hrms.BiometricDevice.api;

import com.hrms.BiometricDevice.application.CreateManufacturerUseCase;
import com.hrms.BiometricDevice.application.GetAllManufacturerUseCase;
import com.hrms.BiometricDevice.application.GetManufacturerByIdUseCase;
import com.hrms.BiometricDevice.application.StatusManufacturerUseCase;
import com.hrms.BiometricDevice.application.UpdateManufacturerUseCase;
import com.hrms.BiometricDevice.domain.Manufacturer;
import com.hrms.BiometricDevice.dto.ManufacturerRequest;
import com.hrms.BiometricDevice.dto.ManufacturerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final CreateManufacturerUseCase createManufacturerUseCase;

    private final UpdateManufacturerUseCase updateManufacturerUseCase;

    private final StatusManufacturerUseCase statusManufacturerUseCase;

    private final GetAllManufacturerUseCase getAllManufacturerUseCase;

    private final GetManufacturerByIdUseCase getManufacturerByIdUseCase;


    // CREATE
    @PostMapping("/create")
    public ResponseEntity<Manufacturer> create(
            @RequestBody ManufacturerRequest request) {

        return ResponseEntity.ok(
                createManufacturerUseCase.execute(request)
        );
    }


    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<Manufacturer> update(
            @PathVariable Long id,
            @RequestBody ManufacturerRequest request) {

        return ResponseEntity.ok(
                updateManufacturerUseCase.execute(id, request)
        );
    }


    // STATUS
    @PutMapping("/status/{id}")
    public ResponseEntity<Manufacturer> changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean status) {

        return ResponseEntity.ok(
                statusManufacturerUseCase.execute(id, status)
        );
    }


    // GET ALL
    @GetMapping("/list")
    public ResponseEntity<List<ManufacturerResponse>> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {

        return ResponseEntity.ok(
                getAllManufacturerUseCase.execute(flag)
        );
    }


    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                getManufacturerByIdUseCase.execute(id)
        );
    }
}
