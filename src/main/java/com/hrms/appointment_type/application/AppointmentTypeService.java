package com.hrms.Appointment_Type.application;

import com.hrms.Appointment_Type.domain.Appointment_Type;
import com.hrms.Appointment_Type.infrastructure.AppointmentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentTypeService {

    private final AppointmentTypeRepository repository;

    public AppointmentTypeService(AppointmentTypeRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public Appointment_Type create(Appointment_Type appointmentType) {
        appointmentType.setIsDeleted(false); // ✅ important default
        return repository.save(appointmentType);
    }

    // GET ALL (ACTIVE ONLY)
    public List<Appointment_Type> getAllByFlag(int flag) {

        // if flag = 0 → active records
        if (flag == 0) {
            return repository.findByIsDeleted(false);
        }

        // if flag = 1 → deleted records
        return repository.findByIsDeleted(true);
    }

    // GET BY ID (only active record recommended)
    public Appointment_Type getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment Type Not Found"));
    }

    // UPDATE
    public Appointment_Type update(Long id, Appointment_Type request) {

        Appointment_Type entity = getById(id);

        entity.setAppointmentType(request.getAppointmentType());
        entity.setUpdatedBy(request.getUpdatedBy());

        return repository.save(entity);
    }

    // SOFT DELETE
    public void delete(Long id) {

        Appointment_Type entity = getById(id);

        entity.setIsDeleted(true);

        repository.save(entity);
    }
}