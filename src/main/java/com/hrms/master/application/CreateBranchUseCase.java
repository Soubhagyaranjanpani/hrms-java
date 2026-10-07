package com.hrms.master.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Branch;
import com.hrms.master.dto.BranchCreateReq;
import com.hrms.master.infrastructure.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateBranchUseCase {

    private final BranchRepository branchRepository;
    private final AuditService auditService;

    public ApiResponse<DefaultResponse> execute(BranchCreateReq request) {

        if (branchRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Branch code already exists");
        }

        if (branchRepository.existsByName(request.getName())) {
            throw new RuntimeException("Branch name already exists");
        }

        Branch branch = new Branch();
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setCountry(request.getCountry());
        branch.setPincode(request.getPincode());

        Branch savedBranch = branchRepository.save(branch);

        // Audit Log - Using existing AuditService
        // Since Branch is not employee-specific, we pass null for Employee
        // and use the full method to capture details
        auditService.log(
                AuditModule.EMPLOYEE,  // Using EMPLOYEE module or you can use any existing module
                AuditAction.CREATE,
                "Branch created: " + savedBranch.getName() +
                        " (" + savedBranch.getCode() + ")",
                null,                    // No subject employee for branch creation
                "Branch",                // fieldChanged
                null,                    // oldValue - nothing existed before
                savedBranch.getName(),   // newValue - the created branch name
                "New branch created with code: " + savedBranch.getCode() +
                        ", City: " + savedBranch.getCity(),  // remarks
                savedBranch.getId()      // referenceId
        );

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Branch Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}