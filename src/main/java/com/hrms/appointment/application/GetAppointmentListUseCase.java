package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.dto.AppointmentPageResponse;
import com.hrms.appointment.dto.AppointmentRecordResponse;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAppointmentListUseCase {

    private final AppointmentRepository repo;
    private final AppointmentMapper mapper;

    public AppointmentPageResponse execute(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AppointmentRecord> resultPage;

        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsDeletedFalse(pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrDesignationOrDepartment(search.trim(), pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public AppointmentPageResponse executeByFlag(Integer flag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Boolean active = (flag == null || flag == 1);

        Page<AppointmentRecord> resultPage;
        if (search == null || search.trim().isEmpty()) {
            resultPage = repo.findByIsActiveAndIsDeletedFalse(active, pageable);
        } else {
            resultPage = repo.searchByOrderNumberOrDesignationOrDepartmentAndIsActive(search.trim(), active, pageable);
        }

        return toPageResponse(resultPage, page, size);
    }

    public List<AppointmentRecordResponse> executeAllByFlag(Integer flag) {
        Boolean active = (flag == null || flag == 1);
        return repo.findByIsActiveAndIsDeletedFalse(active)
                .stream().map(mapper::toResponse).toList();
    }

    private AppointmentPageResponse toPageResponse(Page<AppointmentRecord> resultPage, int page, int size) {
        List<AppointmentRecordResponse> content = resultPage.getContent()
                .stream().map(mapper::toResponse).toList();

        AppointmentPageResponse response = new AppointmentPageResponse();
        response.setContent(content);
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);
        return response;
    }
}
