package com.hrms.disciplinary.application;

import com.hrms.disciplinary.domain.DisciplinaryRecord;
import com.hrms.disciplinary.dto.DisciplinaryPageResponse;
import com.hrms.disciplinary.dto.DisciplinaryRecordResponse;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDisciplinaryListUseCase {

    private final DisciplinaryRepository repo;
    private final DisciplinaryMapper mapper;

    public DisciplinaryPageResponse execute(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DisciplinaryRecord> resultPage;

        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsDeletedFalse(pageable);
        } else {
            resultPage = repo.searchByCaseNumberOrEmployeeOrAction(search.trim(), pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public DisciplinaryPageResponse executeByFlag(Integer flag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Boolean active = (flag == null || flag == 1);

        Page<DisciplinaryRecord> resultPage;
        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsActiveAndIsDeletedFalse(active, pageable);
        } else {
            resultPage = repo.searchByCaseNumberOrEmployeeOrActionAndIsActive(search.trim(), active, pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public List<DisciplinaryRecordResponse> executeAllByFlag(Integer flag) {
        Boolean active = (flag == null || flag == 1);
        return repo.findByIsActiveAndIsDeletedFalse(active)
                .stream().map(mapper::toResponse).toList();
    }

    private DisciplinaryPageResponse toPageResponse(Page<DisciplinaryRecord> resultPage, int page, int size) {
        List<DisciplinaryRecordResponse> content = resultPage.getContent()
                .stream().map(mapper::toResponse).toList();

        DisciplinaryPageResponse response = new DisciplinaryPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }
}