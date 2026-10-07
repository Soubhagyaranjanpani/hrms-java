package com.hrms.promotion.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.promotion.application.*;
import com.hrms.promotion.dto.*;
import com.hrms.promotion.infrastructure.PromotionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final GetPromotionListUseCase getListUseCase;
    private final GetPromotionDashboardStatsUseCase getStatsUseCase;
    private final GetPromotionStatsUseCase getSimpleStatsUseCase;
    private final GetEmployeePromotionHistoryUseCase historyUseCase;
    private final GetDistinctYearsUseCase distinctYearsUseCase;
    private final CreatePromotionUseCase createUseCase;
    private final UpdatePromotionRecordUseCase updateUseCase;
    private final UploadPromotionDocumentUseCase uploadDocumentUseCase;
    private final DownloadPromotionDocumentUseCase downloadDocumentUseCase;
    private final PromotionRepository promoRepo;
    private final PromotionMapper mapper;
    private final SetPromotionStatusUseCase setStatusUseCase;
//    private final SavePromotionUseCase savePromotionUseCase;

    @GetMapping
    public ApiResponse<PromotionPageResponse> getList(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.execute(search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/by-flag")
    public ApiResponse<PromotionPageResponse> getPromotionsByFlag(
            @RequestParam Integer flag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeByFlag(flag, search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/all")
    public ApiResponse<List<PromotionRecordResponse>> getAllPromotions(
            @RequestParam Integer flag) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeAllByFlag(flag), new TypeReference<>() {
                });
    }

    @GetMapping("/data")
    public ApiResponse<?> getPromotionData(
            @RequestParam int flag,
            @RequestParam(required = false) Long id) {

        if (flag == 0) {
            List<PromotionRecordResponse> all = promoRepo.findAll()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createSuccessResponse(all, new TypeReference<>() {
            });
        } else if (flag == 1) {
            if (id == null) {
                throw new IllegalArgumentException("ID is required when flag=1");
            }
            PromotionRecordResponse response = promoRepo.findById(id)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new RuntimeException("Record not found"));
            return ResponseUtils.createSuccessResponse(response, new TypeReference<>() {
            });
        } else {
            throw new IllegalArgumentException("Flag must be 0 or 1");
        }
    }

    @GetMapping("/stats")
    public ApiResponse<PromotionDashboardStats> getDashboardStats(
            @RequestParam(defaultValue = "") String year) {
        return ResponseUtils.createSuccessResponse(getStatsUseCase.execute(year), new TypeReference<>() {
        });
    }

    @GetMapping("/stats/simple")
    public ApiResponse<PromotionStatsResponse> getSimpleStats(
            @RequestParam(defaultValue = "") String year) {
        return ResponseUtils.createSuccessResponse(getSimpleStatsUseCase.execute(year), new TypeReference<>() {
        });
    }

    @GetMapping("/years")
    public ApiResponse<List<String>> getYears() {
        return ResponseUtils.createSuccessResponse(distinctYearsUseCase.execute(), new TypeReference<>() {
        });
    }

    @GetMapping("/{id}")
    public ApiResponse<PromotionRecordResponse> getOne(@PathVariable Long id) {
        PromotionRecordResponse r = promoRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return ResponseUtils.createSuccessResponse(r, new TypeReference<>() {
        });
    }

    @GetMapping("/employee/{empId}")
    public ApiResponse<List<PromotionRecordResponse>> getEmployeeHistory(@PathVariable Long empId) {
        return ResponseUtils.createSuccessResponse(historyUseCase.execute(empId), new TypeReference<>() {
        });
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<PromotionRecordResponse> create(@RequestBody CreatePromotionRequest req) {
        return ResponseUtils.createSuccessResponse(createUseCase.execute(req), new TypeReference<>() {
        });
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<PromotionRecordResponse> update(
            @PathVariable Long id,
            @RequestBody UpdatePromotionRequest req) {
        return ResponseUtils.createSuccessResponse(updateUseCase.execute(id, req), new TypeReference<>() {
        });
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<String> setStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        setStatusUseCase.execute(id, active);
        return ResponseUtils.createSuccessResponse(
                "Status updated to " + (active ? "ACTIVE" : "INACTIVE"),
                new TypeReference<>() {
                });
    }

    @PostMapping("/{id}/document")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<String> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        uploadDocumentUseCase.execute(id, file);
        return ResponseUtils.createSuccessResponse("Document uploaded successfully", new TypeReference<>() {
        });
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) throws Exception {
        DownloadPromotionDocumentUseCase.DocumentFile file = downloadDocumentUseCase.execute(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getBytes());
    }



}