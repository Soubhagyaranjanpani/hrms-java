package com.hrms.Appointment_Type.infrastructure;

import com.hrms.Appointment_Type.domain.Appointment_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentTypeRepository
        extends JpaRepository<Appointment_Type, Long> {

    // ✅ for soft delete filtering
    List<Appointment_Type> findByIsDeleted(boolean isDeleted);
}