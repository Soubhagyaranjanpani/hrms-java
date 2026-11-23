
package com.hrms.EmployeeVerification.api;

import com.hrms.EmployeeVerification.application.ApplyVerificationUseCase;
import com.hrms.EmployeeVerification.application.ReviewVerificationUseCase;
import com.hrms.EmployeeVerification.domain.VerificationStatus;
import com.hrms.EmployeeVerification.dto.SubmitVerificationDto;
import com.hrms.EmployeeVerification.dto.VerificationReviewReq;
import com.hrms.EmployeeVerification.infrastructure.EmployeeVerificationRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-verification")
@RequiredArgsConstructor
public class EmployeeVerificationController {

    private final ApplyVerificationUseCase applyUseCase;
    private final ReviewVerificationUseCase reviewUseCase;
    private final EmployeeVerificationRepository verificationRepo;

    @PostMapping("/apply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> apply(
            Principal principal,
            @Valid @RequestBody SubmitVerificationDto dto) {

        Long id = applyUseCase.execute(
                principal.getName(), dto);

        return ResponseEntity.ok(Map.of(
                "message", "Request submitted successfully",
                "requestId", id,
                "status", "PENDING_VERIFICATION"
        ));
    }

    @GetMapping("/pending-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<?> pendingCount() {

        long count = verificationRepo
                .countByStatusAndIsDeletedFalse(
                        VerificationStatus.PENDING_VERIFICATION);

        return ResponseEntity.ok(
                Map.of("pendingCount", count));
    }

    @PutMapping("/review/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<?> review(
            @PathVariable Long id,
            Principal principal,
            @Valid @RequestBody VerificationReviewReq dto) {

        String status = reviewUseCase.execute(
                id, principal.getName(), dto);

        return ResponseEntity.ok(Map.of(
                "requestId", id,
                "status", status
        ));
    }
}
