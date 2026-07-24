package com.hrms.Confirmation.application;

import com.hrms.Confirmation.application.ConfirmationMapper;
import com.hrms.Confirmation.dto.ConfirmationRecordResponse;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeeConfirmationHistoryUseCase {

    private final ConfirmationRepository repo;
    private final ConfirmationMapper mapper;

    public List<ConfirmationRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByConfirmationDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}
