package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetAppointmentStatusUseCase {

    private final AppointmentRepository repository;

    public void execute(Long id, boolean active) {
        AppointmentRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment record not found with id: " + id));

        record.setIsActive(active);
        repository.save(record);
    }
}
