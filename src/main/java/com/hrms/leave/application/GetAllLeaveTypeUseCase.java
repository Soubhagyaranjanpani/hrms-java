package com.hrms.leave.application;

import com.hrms.leave.domain.LeaveType;
import com.hrms.leave.infrastructure.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllLeaveTypeUseCase {

    private final LeaveTypeRepository repo;

    public List<LeaveType> execute() {
        return repo.findAll();
    }
}
