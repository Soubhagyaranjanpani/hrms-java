package com.hrms.retirement.application;

import com.hrms.master.infrastructure.RetirementTypeRepository;
import com.hrms.retirement.dto.MasterOptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRetirementTypesUseCase {

    private final RetirementTypeRepository repo;

    public List<MasterOptionResponse> execute() {
        return repo.findByIsActiveTrue().stream().map(t -> {
            MasterOptionResponse res = new MasterOptionResponse();
            res.setId(t.getId());
            res.setName(t.getName());
            return res;
        }).toList();
    }
}
