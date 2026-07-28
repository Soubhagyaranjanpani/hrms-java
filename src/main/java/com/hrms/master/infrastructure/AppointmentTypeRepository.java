package com.hrms.master.infrastructure;

import com.hrms.master.domain.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, Long> {

    boolean existsByAppointmentType(String appointmentType);

    List<AppointmentType> findByIsActiveTrue();
}