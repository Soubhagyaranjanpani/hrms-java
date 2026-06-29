package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeAppointmentRepository
        extends JpaRepository<EmployeeAppointment, Long> {

    List<EmployeeAppointment> findByIsDeleted(Boolean isDeleted);
}