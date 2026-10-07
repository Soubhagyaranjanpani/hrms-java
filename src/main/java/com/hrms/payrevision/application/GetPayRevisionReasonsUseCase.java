package com.hrms.payrevision.application;

import com.hrms.master.domain.RevisionReason;
import com.hrms.master.infrastructure.RevisionReasonRepository;
import com.hrms.payrevision.dto.PayRevisionReasonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPayRevisionReasonsUseCase {

    private final RevisionReasonRepository reasonRepo;

    public List<PayRevisionReasonResponse> execute() {
        return reasonRepo.findByIsActiveTrue().stream().map(reason -> {
            PayRevisionReasonResponse res = new PayRevisionReasonResponse();
            res.setId(reason.getId());
            res.setName(reason.getName());
            return res;
        }).toList();
    }
}