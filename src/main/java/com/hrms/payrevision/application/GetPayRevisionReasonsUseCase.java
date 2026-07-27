package com.hrms.payrevision.application;

import com.hrms.payrevision.dto.PayRevisionReasonResponse;
import com.hrms.payrevision.infrastructure.PayRevisionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPayRevisionReasonsUseCase {

    private final PayRevisionReasonRepository repo;

    public List<PayRevisionReasonResponse> execute() {
        return repo.findByIsActiveTrue().stream().map(reason -> {
            PayRevisionReasonResponse res = new PayRevisionReasonResponse();
            res.setId(reason.getId());
            res.setName(reason.getName());
            return res;
        }).toList();
    }
}
