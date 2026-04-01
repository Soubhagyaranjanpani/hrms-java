package com.hrms.leave.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.application.HolidayBulkUploadService;
import com.hrms.leave.application.HolidayUseCase;
import com.hrms.leave.dto.HolidayRequest;
import com.hrms.leave.dto.HolidayResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayUseCase holidayUseCase;
    private final HolidayBulkUploadService holidayBulkUploadService;

    // 🔥 CREATE
    @Operation(summary = "Create holiday")
    @PostMapping
    public ApiResponse<HolidayResponse> create(@RequestBody HolidayRequest request) {

        HolidayResponse data = holidayUseCase.create(request);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<>() {}
        );
    }

    // 🔥 GET ALL
    @Operation(summary = "Get all holidays")
    @GetMapping
    public ApiResponse<List<HolidayResponse>> getAll() {

        List<HolidayResponse> data = holidayUseCase.getAll();

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<>() {}
        );
    }

    // 🔥 DELETE
    @Operation(summary = "Delete holiday")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {

        holidayUseCase.delete(id);

        return ResponseUtils.createSuccessResponse(
                "Holiday deleted successfully",
                new TypeReference<>() {}
        );
    }

    // 🔥 BULK UPLOAD
    @Operation(summary = "Upload holidays via file")
    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {

        String result = holidayBulkUploadService.upload(file);

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }
}
