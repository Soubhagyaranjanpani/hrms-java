package com.hrms.master.application;



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

        branchRepository.save(branch);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Branch Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
