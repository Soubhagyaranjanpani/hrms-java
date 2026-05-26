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
@RequestMapping("/api/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final CreateDesignationUseCase createDesignationUseCase;
    private final UpdateDesignationUseCase updateDesignationUseCase;
    private final ChangeDesignationStatusUseCase changeDesignationStatusUseCase;
    private final GetDesignationUseCase getDesignationUseCase;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> create(
            @RequestBody DesignationCreateReq request) {
        return ResponseEntity.ok(createDesignationUseCase.execute(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<DefaultResponse>> update(
            @RequestBody DesignationUpdateReq request) {
        return ResponseEntity.ok(updateDesignationUseCase.execute(request));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<DefaultResponse>> changeStatus(
            @PathVariable Long id) {
        return ResponseEntity.ok(changeDesignationStatusUseCase.execute(id));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DesignationResponse>>> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return ResponseEntity.ok(getDesignationUseCase.execute(flag));
    }
}
