package com.hrms.retirement.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.retirement.application.*;
import com.hrms.retirement.dto.*;
import com.hrms.retirement.infrastructure.RetirementRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
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
@RequestMapping("/api/retirements")
@RequiredArgsConstructor
public class RetirementController {

    private final GetRetirementListUseCase getListUseCase;
    private final GetEmployeeRetirementHistoryUseCase historyUseCase;
    private final GetRetirementTypesUseCase getRetirementTypesUseCase;
    private final GetPensionEligibilityOptionsUseCase getPensionEligibilityOptionsUseCase;
    private final CreateRetirementUseCase createUseCase;
    private final UpdateRetirementRecordUseCase updateUseCase;
    private final UploadRetirementDocumentUseCase uploadDocumentUseCase;
    private final DownloadRetirementDocumentUseCase downloadDocumentUseCase;
    private final RetirementRepository retirementRepo;
    private final RetirementMapper mapper;
    private final SetRetirementStatusUseCase setStatusUseCase;

    // ── Master dropdowns ──
    @GetMapping("/retirement-types")
    public ApiResponse<List<MasterOptionResponse>> getRetirementTypes() {
        return ResponseUtils.createSuccessResponse(getRetirementTypesUseCase.execute(), new TypeReference<>() {
        });
    }

    @GetMapping("/pension-eligibility-options")
    public ApiResponse<List<MasterOptionResponse>> getPensionEligibilityOptions() {
        return ResponseUtils.createSuccessResponse(getPensionEligibilityOptionsUseCase.execute(), new TypeReference<>() {
        });
    }

    @GetMapping
    public ApiResponse<RetirementPageResponse> getList(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.execute(search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/by-flag")
    public ApiResponse<RetirementPageResponse> getRetirementsByFlag(
            @RequestParam Integer flag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeByFlag(flag, search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/all")
    public ApiResponse<List<RetirementRecordResponse>> getAllRetirements(
            @RequestParam Integer flag) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeAllByFlag(flag), new TypeReference<>() {
                });
    }

    @GetMapping("/data")
    public ApiResponse<?> getRetirementData(
            @RequestParam int flag,
            @RequestParam(required = false) Long id) {

        if (flag == 0) {
            List<RetirementRecordResponse> all = retirementRepo.findAll()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createSuccessResponse(all, new TypeReference<>() {
            });
        } else if (flag == 1) {
            if (id == null) {
                throw new IllegalArgumentException("ID is required when flag=1");
            }
            RetirementRecordResponse response = retirementRepo.findById(id)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new RuntimeException("Record not found"));
            return ResponseUtils.createSuccessResponse(response, new TypeReference<>() {
            });
        } else {
            throw new IllegalArgumentException("Flag must be 0 or 1");
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<RetirementRecordResponse> getOne(@PathVariable Long id) {
        RetirementRecordResponse r = retirementRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return ResponseUtils.createSuccessResponse(r, new TypeReference<>() {
        });
    }

    @GetMapping("/employee/{empId}")
    public ApiResponse<List<RetirementRecordResponse>> getEmployeeHistory(@PathVariable Long empId) {
        return ResponseUtils.createSuccessResponse(historyUseCase.execute(empId), new TypeReference<>() {
        });
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<RetirementRecordResponse> create(@RequestBody CreateRetirementRequest req) {
        return ResponseUtils.createSuccessResponse(createUseCase.execute(req), new TypeReference<>() {
        });
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<RetirementRecordResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateRetirementRequest req) {
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
        DownloadRetirementDocumentUseCase.DocumentFile file = downloadDocumentUseCase.execute(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getBytes());
    }
}
