package com.hrms.payrevision.application;

import com.hrms.payrevision.dto.PayRevisionRecordResponse;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeePayRevisionHistoryUseCase {

    private final PayRevisionRepository repo;
    private final PayRevisionMapper mapper;

    public List<PayRevisionRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByEffectiveDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}
