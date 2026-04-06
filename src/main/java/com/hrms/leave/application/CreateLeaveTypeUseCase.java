package com.hrms.leave.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.domain.LeaveType;
import com.hrms.leave.infrastructure.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateLeaveTypeUseCase {
    private final InitializeLeaveBalanceForAllEmployeesUseCase initAllUseCase;

    private final LeaveTypeRepository repo;

    public ApiResponse<String> execute(LeaveType request) {

        if (repo.existsByName(request.getName())) {
            return ResponseUtils.createFailureResponse(null,null,"Leave type already exists",400);
        }

        LeaveType type = new LeaveType();
        type.setName(request.getName().toUpperCase());
        type.setMaxDaysPerYear(request.getMaxDaysPerYear());
        type.setIsActive(true);

        repo.save(type);
        initAllUseCase.execute(type);

        return ResponseUtils.createSuccessResponse("Leave type created",null);
    }
}
