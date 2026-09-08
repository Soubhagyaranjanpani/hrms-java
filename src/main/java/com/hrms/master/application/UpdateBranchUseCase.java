package com.hrms.master.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Branch;
import com.hrms.master.dto.BranchUpdateReq;
import com.hrms.master.infrastructure.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateBranchUseCase {

    private final BranchRepository branchRepository;
    private final AuditService auditService;

    public ApiResponse<DefaultResponse> execute(BranchUpdateReq request) {

        Branch branch = branchRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        String oldName = branch.getName();

        // Update branch
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setCountry(request.getCountry());
        branch.setPincode(request.getPincode());

        Branch updatedBranch = branchRepository.save(branch);

        // Audit Log - Simple update entry
        auditService.log(
                AuditModule.EMPLOYEE,
                AuditAction.UPDATE,
                "Branch updated: " + oldName + " → " + updatedBranch.getName(),
                null,
                "Branch Details",
                oldName,
                updatedBranch.getName(),
                "Branch information updated",
                updatedBranch.getId()
        );

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Branch Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}