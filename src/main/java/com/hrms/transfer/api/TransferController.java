
package com.hrms.transfer.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.transfer.application.*;
        import com.hrms.transfer.dto.*;
        import com.hrms.transfer.infrastructure.TransferRepository;
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
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final GetTransferListUseCase getListUseCase;
    private final GetEmployeeTransferHistoryUseCase historyUseCase;
    private final CreateTransferUseCase createUseCase;
    private final UpdateTransferRecordUseCase updateUseCase;
    private final UploadTransferDocumentUseCase uploadDocumentUseCase;
    private final DownloadTransferDocumentUseCase downloadDocumentUseCase;
    private final TransferRepository transferRepo;
    private final TransferMapper mapper;
    private final SetTransferStatusUseCase setStatusUseCase;

    @GetMapping
    public ApiResponse<TransferPageResponse> getList(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.execute(search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/by-flag")
    public ApiResponse<TransferPageResponse> getTransfersByFlag(
            @RequestParam Integer flag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeByFlag(flag, search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/all")
    public ApiResponse<List<TransferRecordResponse>> getAllTransfers(
            @RequestParam Integer flag) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeAllByFlag(flag), new TypeReference<>() {
                });
    }

    @GetMapping("/data")
    public ApiResponse<?> getTransferData(
            @RequestParam int flag,
            @RequestParam(required = false) Long id) {

        if (flag == 0) {
            List<TransferRecordResponse> all = transferRepo.findAll()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createSuccessResponse(all, new TypeReference<>() {
            });
        } else if (flag == 1) {
            if (id == null) {
                throw new IllegalArgumentException("ID is required when flag=1");
            }
            TransferRecordResponse response = transferRepo.findById(id)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new RuntimeException("Record not found"));
            return ResponseUtils.createSuccessResponse(response, new TypeReference<>() {
            });
        } else {
            throw new IllegalArgumentException("Flag must be 0 or 1");
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<TransferRecordResponse> getOne(@PathVariable Long id) {
        TransferRecordResponse r = transferRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return ResponseUtils.createSuccessResponse(r, new TypeReference<>() {
        });
    }

    @GetMapping("/employee/{empId}")
    public ApiResponse<List<TransferRecordResponse>> getEmployeeHistory(@PathVariable Long empId) {
        return ResponseUtils.createSuccessResponse(historyUseCase.execute(empId), new TypeReference<>() {
        });
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<TransferRecordResponse> create(@RequestBody CreateTransferRequest req) {
        return ResponseUtils.createSuccessResponse(createUseCase.execute(req), new TypeReference<>() {
        });
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<TransferRecordResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateTransferRequest req) {
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
        DownloadTransferDocumentUseCase.DocumentFile file = downloadDocumentUseCase.execute(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getBytes());
    }
}
