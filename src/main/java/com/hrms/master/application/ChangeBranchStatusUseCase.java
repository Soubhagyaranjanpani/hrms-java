package com.hrms.master.application;



import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Branch;
import com.hrms.master.infrastructure.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeBranchStatusUseCase {

    private final BranchRepository branchRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        branch.setIsActive(!branch.getIsActive());

        branchRepository.save(branch);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Branch Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
