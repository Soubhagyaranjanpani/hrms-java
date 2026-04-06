package com.hrms.leave.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.domain.LeaveType;
import com.hrms.leave.infrastructure.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateLeaveTypeUseCase {

    private final LeaveTypeRepository repo;

    public ApiResponse<String> execute(Long id, LeaveType request) {

        LeaveType type = repo.findById(id).orElse(null);

        if (type == null) {
            return ResponseUtils.createFailureResponse(null,null,"Leave type not found",404);
        }

        type.setMaxDaysPerYear(request.getMaxDaysPerYear());
        type.setIsActive(request.getIsActive());

        repo.save(type);

        return ResponseUtils.createSuccessResponse("Leave type updated",null);
    }
}
