
package com.hrms.EmployeeVerification.application;

import com.hrms.EmployeeVerification.domain.*;
import com.hrms.EmployeeVerification.dto.VerificationReviewReq;
import com.hrms.EmployeeVerification.infrastructure.EmployeeVerificationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReviewVerificationUseCase {

    private final EmployeeVerificationRepository verificationRepo;

    @Transactional
    public String execute(
            Long requestId,
            String hrEmail,
            VerificationReviewReq dto) {

        EmployeeVerificationRequest request =
                verificationRepo.findLockedById(requestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Verification request not found"));

        if (Boolean.TRUE.equals(request.getIsDeleted())) {
            throw new IllegalStateException("Request is deleted");
        }

        if (request.getStatus() !=
                VerificationStatus.PENDING_VERIFICATION) {
            throw new IllegalStateException(
                    "Request already processed");
        }

        String decision = dto.getDecision()
                .trim().toUpperCase(Locale.ROOT);

        switch (decision) {
            case "APPROVE" -> {
                request.setStatus(VerificationStatus.APPROVED);
                request.setRejectionReason(null);
            }
            case "REJECT" -> {
                if (dto.getRejectionReason() == null ||
                        dto.getRejectionReason().isBlank()) {
                    throw new IllegalArgumentException(
                            "Rejection reason required");
                }

                request.setStatus(VerificationStatus.REJECTED);
                request.setRejectionReason(
                        dto.getRejectionReason());
            }
            default -> throw new IllegalArgumentException(
                    "Decision must be APPROVE or REJECT");
        }

        request.setReviewedBy(hrEmail);
        request.setReviewedAt(LocalDateTime.now());

        verificationRepo.save(request);

        return request.getStatus().name();
    }
}
