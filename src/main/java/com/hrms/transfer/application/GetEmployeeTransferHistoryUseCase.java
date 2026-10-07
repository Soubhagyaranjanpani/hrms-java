package com.hrms.transfer.application;

import com.hrms.transfer.dto.TransferRecordResponse;
import com.hrms.transfer.infrastructure.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeeTransferHistoryUseCase {

    private final TransferRepository repo;
    private final TransferMapper mapper;

    public List<TransferRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByTransferDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}
