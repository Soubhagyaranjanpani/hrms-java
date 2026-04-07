package com.hrms.leave.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.infrastructure.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllLeavesUseCase {

    private final LeaveRepository leaveRepo;

    public ApiResponse<List<Leave>> execute(LeaveStatus status) {

        List<Leave> leaves;

        if (status != null) {
            leaves = leaveRepo.findByStatus(status);
        } else {
            leaves = leaveRepo.findAll();
        }

        return ResponseUtils.createSuccessResponse(
                leaves,
                new TypeReference<>() {}
        );
    }
}
