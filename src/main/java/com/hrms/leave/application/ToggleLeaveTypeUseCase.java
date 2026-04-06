package com.hrms.leave.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.domain.LeaveType;
import com.hrms.leave.infrastructure.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToggleLeaveTypeUseCase {

    private final LeaveTypeRepository repo;

    public ApiResponse<String> execute(Long id, Boolean active) {

        LeaveType type = repo.findById(id).orElse(null);

        if (type == null) {
            return ResponseUtils.createFailureResponse(null,null,"Leave type not found",
                    404);
        }

        type.setIsActive(active);
        repo.save(type);

        return ResponseUtils.createSuccessResponse("Leave type status updated",null);
    }
}
