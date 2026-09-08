package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.dto.AppointmentRecordResponse;
import com.hrms.appointment.dto.UpdateAppointmentRequest;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employment_type.domain.EmploymentType;
import com.hrms.employment_type.infrastructure.EmploymentTypeRepository;
import com.hrms.master.domain.AppointmentType;
import com.hrms.master.infrastructure.AppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateAppointmentRecordUseCase {

    private final AppointmentRepository repo;
    private final AppointmentMapper mapper;
    private final AppointmentTypeRepository appointmentTypeRepo;
    private final EmploymentTypeRepository employmentTypeRepo;
    private final AuditService auditService;

    public AppointmentRecordResponse execute(Long id, UpdateAppointmentRequest req) {
        AppointmentRecord a = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment record not found"));

        // Capture old values for audit
        String oldOrderNumber = a.getAppointmentOrderNumber();
        String oldAppointmentType = a.getAppointmentType() != null ?
                a.getAppointmentType().toString() : null;
        String oldEmploymentType = a.getEmploymentType() != null ?
                a.getEmploymentType().toString() : null;
        String oldRemarks = a.getRemarks();

        // Update fields
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

        AppointmentRecord updatedAppointment = repo.save(a);

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (oldOrderNumber != null && !oldOrderNumber.equals(updatedAppointment.getAppointmentOrderNumber())) {
            changes.add("Order Number: " + oldOrderNumber + " → " +
                    updatedAppointment.getAppointmentOrderNumber());
        }

        if (req.getAppointmentDate() != null) {
            changes.add("Appointment Date updated");
        }

        String newAppointmentType = updatedAppointment.getAppointmentType() != null ?
                updatedAppointment.getAppointmentType().toString() : null;
        if (oldAppointmentType != null && !oldAppointmentType.equals(newAppointmentType)) {
            changes.add("Type: " + oldAppointmentType + " → " + newAppointmentType);
        }

        String newEmploymentType = updatedAppointment.getEmploymentType() != null ?
                updatedAppointment.getEmploymentType().toString() : null;
        if (oldEmploymentType != null && !oldEmploymentType.equals(newEmploymentType)) {
            changes.add("Employment Type: " + oldEmploymentType + " → " + newEmploymentType);
        }

        if (req.getJoiningDate() != null) {
            changes.add("Joining Date updated");
        }

        if (req.getProbationPeriodMonths() != null) {
            changes.add("Probation Period updated");
        }

        if (oldRemarks != null && !oldRemarks.equals(updatedAppointment.getRemarks())) {
            changes.add("Remarks updated");
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.APPOINTMENT,
                    AuditAction.UPDATE,
                    "Appointment record updated for " +
                            (updatedAppointment.getEmployee() != null ?
                                    updatedAppointment.getEmployee().getFullName() +
                                    " (" + updatedAppointment.getEmployee().getEmployeeCode() + ")" :
                                    "record"),
                    updatedAppointment.getEmployee(),
                    "Multiple Fields",
                    oldOrderNumber,
                    updatedAppointment.getAppointmentOrderNumber(),
                    String.join("; ", changes),
                    updatedAppointment.getId()
            );
        }

        return mapper.toResponse(updatedAppointment);
    }
}