package com.hrms.employee.application;

import com.hrms.employee.domain.EmployeeAppointment;
import com.hrms.employee.dto.EmployeeAppointmentRequest;
import com.hrms.employee.infrastructure.EmployeeAppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeAppointmentService {

    private final EmployeeAppointmentRepository repository;

    public EmployeeAppointmentService(EmployeeAppointmentRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public EmployeeAppointment save(EmployeeAppointmentRequest request) {

        EmployeeAppointment appointment = new EmployeeAppointment();

        mapRequestToEntity(request, appointment);

        return repository.save(appointment);
    }

    // GET ALL
    public List<EmployeeAppointment> getAllByFlag(int flag) {

        if (flag == 0) {
            return repository.findByIsDeleted(false);
        }
        return repository.findByIsDeleted(true);
    }

    // GET BY ID
    public EmployeeAppointment getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Appointment Not Found"));
    }

    // UPDATE
    public EmployeeAppointment update(Long id, EmployeeAppointmentRequest request) {

        EmployeeAppointment appointment = getById(id);

        mapRequestToEntity(request, appointment);

        return repository.save(appointment);
    }

    // DELETE
    public void delete(Long id) {

        EmployeeAppointment appointment = getById(id);

        repository.delete(appointment);
    }

    // Common Mapping Method
    private void mapRequestToEntity(EmployeeAppointmentRequest request,
                                    EmployeeAppointment appointment) {

        appointment.setEmployeeId(request.getEmployeeId());
        appointment.setAppointmentOrderNumber(request.getAppointmentOrderNumber());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentAuthorityId(request.getAppointmentAuthorityId());
        appointment.setAppointmentTypeId(request.getAppointmentTypeId());
        appointment.setEmploymentTypeId(request.getEmploymentTypeId());
        appointment.setDesignationId(request.getDesignationId());
        appointment.setDepartmentId(request.getDepartmentId());
        appointment.setBranchId(request.getBranchId());
        appointment.setJoiningDate(request.getJoiningDate());
        appointment.setProbationPeriod(request.getProbationPeriod());
        appointment.setUploadId(request.getUploadId());

        if (request.getJoiningDate() != null &&
                request.getProbationPeriod() != null) {

            appointment.setConfirmationDueDate(
                    request.getJoiningDate()
                            .plusMonths(request.getProbationPeriod())
            );
        }
    }
}