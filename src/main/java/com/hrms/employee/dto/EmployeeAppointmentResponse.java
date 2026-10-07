package com.hrms.employee.dto;

import java.time.LocalDate;

public class EmployeeAppointmentResponse {

    private Long id;

    private String appointmentOrderNumber;

    private LocalDate appointmentDate;

    private LocalDate joiningDate;

    private LocalDate confirmationDueDate;

    public EmployeeAppointmentResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointmentOrderNumber() {
        return appointmentOrderNumber;
    }

    public void setAppointmentOrderNumber(String appointmentOrderNumber) {
        this.appointmentOrderNumber = appointmentOrderNumber;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public LocalDate getConfirmationDueDate() {
        return confirmationDueDate;
    }

    public void setConfirmationDueDate(LocalDate confirmationDueDate) {
        this.confirmationDueDate = confirmationDueDate;
    }
}