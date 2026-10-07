package com.hrms.master.application;



import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Branch;
import com.hrms.master.dto.BranchResponse;
import com.hrms.master.infrastructure.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetBranchUseCase {

    private final BranchRepository branchRepository;

    public ApiResponse<List<BranchResponse>> execute(Integer flag) {

        List<Branch> branches;

        if (flag == 1) {
            branches = branchRepository.findByIsActiveTrueAndIsDeletedFalse();
        } else {
            branches = branchRepository.findByIsDeletedFalse();
        }

        List<BranchResponse> response = branches.stream().map(b -> {
            BranchResponse res = new BranchResponse();
            res.setId(b.getId());
            res.setCode(b.getCode());
            res.setName(b.getName());
            res.setAddress(b.getAddress());
            res.setCity(b.getCity());
            res.setState(b.getState());
            res.setCountry(b.getCountry());
            res.setPincode(b.getPincode());
            res.setIsActive(b.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}
