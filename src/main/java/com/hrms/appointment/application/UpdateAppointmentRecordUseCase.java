package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.dto.AppointmentRecordResponse;
import com.hrms.appointment.dto.UpdateAppointmentRequest;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class UpdateAppointmentRecordUseCase {

    private final AppointmentRepository repo;
    private final AppointmentMapper mapper;

    public AppointmentRecordResponse execute(Long id, UpdateAppointmentRequest req) {
        AppointmentRecord a = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment record not found"));

        if (req.getAppointmentOrderNumber() != null) a.setAppointmentOrderNumber(req.getAppointmentOrderNumber());
        if (req.getAppointmentDate() != null) a.setAppointmentDate(req.getAppointmentDate());
        if (req.getAppointmentType() != null) a.setAppointmentType(req.getAppointmentType());
        if (req.getEmploymentType() != null) a.setEmploymentType(req.getEmploymentType());
        if (req.getJoiningDate() != null) a.setJoiningDate(req.getJoiningDate());
        if (req.getProbationPeriodMonths() != null) a.setProbationPeriodMonths(req.getProbationPeriodMonths());
        if (req.getRemarks() != null) a.setRemarks(req.getRemarks());

        return mapper.toResponse(repo.save(a));
    }
}
