package com.hrms.appointment.application;

import com.hrms.appointment.dto.AppointmentRecordResponse;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeeAppointmentHistoryUseCase {

    private final AppointmentRepository repo;
    private final AppointmentMapper mapper;

    public List<AppointmentRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByAppointmentDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}


