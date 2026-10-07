package com.hrms.disciplinary.application;

import com.hrms.disciplinary.dto.DisciplinaryRecordResponse;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeeDisciplinaryHistoryUseCase {

    private final DisciplinaryRepository repo;
    private final DisciplinaryMapper mapper;

    public List<DisciplinaryRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByIncidentDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}