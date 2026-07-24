package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.dto.AppointmentRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentRecordResponse toResponse(AppointmentRecord a) {
        AppointmentRecordResponse r = new AppointmentRecordResponse();
        r.setId(a.getId());
        r.setEmployeeId(a.getEmployee().getId());

        // use getFullName() — avoids the "firstNamenullLastName" bug seen in Promotion before its fix
        r.setEmployee(a.getEmployee().getFullName());
        r.setEmployeeCode(a.getEmployee().getEmployeeCode());

        r.setAppointmentOrderNumber(a.getAppointmentOrderNumber());
        r.setAppointmentDate(a.getAppointmentDate());

        if (a.getAppointmentAuthority() != null && a.getAppointmentAuthority().getEmployee() != null) {
            r.setAppointmentAuthority(a.getAppointmentAuthority().getEmployee().getFullName());
        } else {
            r.setAppointmentAuthority("—");
        }
        if (a.getAppointmentAuthority() != null && a.getAppointmentAuthority().getDesignation() != null) {
            r.setAppointmentAuthorityDesignation(a.getAppointmentAuthority().getDesignation().getName());
        } else {
            r.setAppointmentAuthorityDesignation("—");
        }

        r.setAppointmentType(a.getAppointmentType());
        r.setEmploymentType(a.getEmploymentType());

        r.setDesignation(a.getInitialDesignation() != null ? a.getInitialDesignation().getName() : "—");
        r.setDepartment(a.getInitialDepartment() != null ? a.getInitialDepartment().getName() : "—");
        r.setBranch(a.getInitialBranch() != null ? a.getInitialBranch().getName() : "—");

        r.setJoiningDate(a.getJoiningDate());
        r.setProbationPeriodMonths(a.getProbationPeriodMonths());
        r.setConfirmationDueDate(a.getConfirmationDueDate());

        r.setIsActive(a.getIsActive());

        r.setDocumentPath(a.getDocumentPath());
        r.setDocumentName(a.getDocumentName());

        r.setRemarks(a.getRemarks());
        r.setProcessedBy(a.getProcessedBy());

        return r;
    }
}


