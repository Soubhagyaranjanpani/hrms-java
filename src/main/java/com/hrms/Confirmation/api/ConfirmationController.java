package com.hrms.Confirmation.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.Confirmation.application.*;
import com.hrms.Confirmation.application.*;
import com.hrms.Confirmation.dto.*;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
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
@RequestMapping("/api/confirmations")
@RequiredArgsConstructor
public class ConfirmationController {

    private final GetConfirmationListUseCase getListUseCase;
    private final GetEmployeeConfirmationHistoryUseCase historyUseCase;
    private final CreateConfirmationUseCase createUseCase;
    private final UpdateConfirmationRecordUseCase updateUseCase;
    private final UploadConfirmationDocumentUseCase uploadDocumentUseCase;
    private final DownloadConfirmationDocumentUseCase downloadDocumentUseCase;
    private final ConfirmationRepository confirmationRepo;
    private final ConfirmationMapper mapper;
    private final SetConfirmationStatusUseCase setStatusUseCase;

    @GetMapping
    public ApiResponse<ConfirmationPageResponse> getList(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.execute(search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/by-flag")
    public ApiResponse<ConfirmationPageResponse> getConfirmationsByFlag(
            @RequestParam Integer flag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeByFlag(flag, search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/all")
    public ApiResponse<List<ConfirmationRecordResponse>> getAllConfirmations(
            @RequestParam Integer flag) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeAllByFlag(flag), new TypeReference<>() {
                });
    }

    @GetMapping("/data")
    public ApiResponse<?> getConfirmationData(
            @RequestParam int flag,
            @RequestParam(required = false) Long id) {

        if (flag == 0) {
            List<ConfirmationRecordResponse> all = confirmationRepo.findAll()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createSuccessResponse(all, new TypeReference<>() {
            });
        } else if (flag == 1) {
            if (id == null) {
                throw new IllegalArgumentException("ID is required when flag=1");
            }
            ConfirmationRecordResponse response = confirmationRepo.findById(id)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new RuntimeException("Record not found"));
            return ResponseUtils.createSuccessResponse(response, new TypeReference<>() {
            });
        } else {
            throw new IllegalArgumentException("Flag must be 0 or 1");
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ConfirmationRecordResponse> getOne(@PathVariable Long id) {
        ConfirmationRecordResponse r = confirmationRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return ResponseUtils.createSuccessResponse(r, new TypeReference<>() {
        });
    }

    @GetMapping("/employee/{empId}")
    public ApiResponse<List<ConfirmationRecordResponse>> getEmployeeHistory(@PathVariable Long empId) {
        return ResponseUtils.createSuccessResponse(historyUseCase.execute(empId), new TypeReference<>() {
        });
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<ConfirmationRecordResponse> create(@RequestBody CreateConfirmationRequest req) {
        return ResponseUtils.createSuccessResponse(createUseCase.execute(req), new TypeReference<>() {
        });
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<ConfirmationRecordResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateConfirmationRequest req) {
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
        DownloadConfirmationDocumentUseCase.DocumentFile file = downloadDocumentUseCase.execute(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getBytes());
    }
}
