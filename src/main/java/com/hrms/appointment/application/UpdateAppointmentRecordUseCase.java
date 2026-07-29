package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.dto.AppointmentRecordResponse;
import com.hrms.appointment.dto.UpdateAppointmentRequest;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import com.hrms.employment_type.domain.EmploymentType;
import com.hrms.employment_type.infrastructure.EmploymentTypeRepository;
import com.hrms.master.domain.AppointmentType;
import com.hrms.master.infrastructure.AppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAppointmentRecordUseCase {

    private final AppointmentRepository repo;
    private final AppointmentMapper mapper;
    private final AppointmentTypeRepository appointmentTypeRepo;
    private final EmploymentTypeRepository employmentTypeRepo;

    public AppointmentRecordResponse execute(Long id, UpdateAppointmentRequest req) {
        AppointmentRecord a = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment record not found"));

        if (req.getAppointmentOrderNumber() != null) a.setAppointmentOrderNumber(req.getAppointmentOrderNumber());
        if (req.getAppointmentDate() != null) a.setAppointmentDate(req.getAppointmentDate());

        if (req.getAppointmentTypeId() != null) {
            AppointmentType appointmentType = appointmentTypeRepo.findById(req.getAppointmentTypeId())
                    .orElseThrow(() -> new RuntimeException("Appointment type not found"));
            a.setAppointmentType(appointmentType);
        }

        if (req.getEmploymentTypeId() != null) {
            EmploymentType employmentType = employmentTypeRepo.findById(req.getEmploymentTypeId())
                    .orElseThrow(() -> new RuntimeException("Employment type not found"));
            a.setEmploymentType(employmentType);
        }

        if (req.getJoiningDate() != null) a.setJoiningDate(req.getJoiningDate());
        if (req.getProbationPeriodMonths() != null) a.setProbationPeriodMonths(req.getProbationPeriodMonths());
        if (req.getRemarks() != null) a.setRemarks(req.getRemarks());

        return mapper.toResponse(repo.save(a));
    }
}