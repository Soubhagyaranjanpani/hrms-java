package com.hrms.appointment_type.application;

import com.hrms.appointment_type.domain.AppointmentAuthority;
import com.hrms.appointment_type.infrastructure.AppointmentAuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentAuthorityService {

    @Autowired
    private AppointmentAuthorityRepository repository;

    // Create
    public AppointmentAuthority create(AppointmentAuthority authority) {
        authority.setFlag(0);
        return repository.save(authority);
    }

    // Get All By Flag
    public List<AppointmentAuthority> getAllByFlag(int flag) {
        return repository.findByFlag(flag);
    }

    // Get By Id
    public AppointmentAuthority getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Authority not found with id: " + id));
    }

    // Update
    public AppointmentAuthority update(Long id, AppointmentAuthority authority) {

        AppointmentAuthority existing = getById(id);

        existing.setAuthorityName(authority.getAuthorityName());
        existing.setDepartment(authority.getDepartment());
        existing.setEmail(authority.getEmail());

        return repository.save(existing);
    }

    // Soft Delete
    public void delete(Long id) {

        AppointmentAuthority existing = getById(id);

        existing.setFlag(1);

        repository.save(existing);
    }

}
