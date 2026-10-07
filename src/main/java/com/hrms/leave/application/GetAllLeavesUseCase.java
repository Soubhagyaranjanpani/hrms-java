package com.hrms.leave.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.dto.LeaveResponse;
import com.hrms.leave.infrastructure.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllLeavesUseCase {

    private final LeaveRepository leaveRepo;

    public ApiResponse<List<LeaveResponse>> execute(LeaveStatus status, int page, int size) {

        PageRequest pageable = PageRequest.of(page, size);  // ✅ FIX

        Page<LeaveResponse> leaves =
                leaveRepo.findAllProjected(status, pageable);

        return ResponseUtils.createSuccessResponse(
                leaves.getContent(),
                new TypeReference<>() {}
        );
    }
}
