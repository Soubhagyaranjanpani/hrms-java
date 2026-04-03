package com.hrms.master.application;



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

    public ApiResponse<DefaultResponse> execute(BranchUpdateReq request) {

        Branch branch = branchRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setCountry(request.getCountry());
        branch.setPincode(request.getPincode());

        branchRepository.save(branch);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Branch Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
