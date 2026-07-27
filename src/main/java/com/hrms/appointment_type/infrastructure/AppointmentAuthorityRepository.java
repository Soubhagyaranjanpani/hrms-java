package com.hrms.appointment_type.infrastructure;

import com.hrms.appointment_type.domain.AppointmentAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentAuthorityRepository
        extends JpaRepository<AppointmentAuthority, Long> {

    List<AppointmentAuthority> findByFlag(Integer flag);
}