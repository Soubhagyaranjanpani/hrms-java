package com.hrms.employee.dto;

import java.time.LocalDate;

public class EmployeeAppointmentRequest {

    private Long employeeId;
    private String appointmentOrderNumber;
    private LocalDate appointmentDate;
    private Long appointmentAuthorityId;
    private Long appointmentTypeId;
    private Long employmentTypeId;
    private Long designationId;
    private Long departmentId;
    private Long branchId;
    private LocalDate joiningDate;
    private Integer probationPeriod;
    private Long uploadId;

    // Getters and Setters

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public Long getAppointmentAuthorityId() {
        return appointmentAuthorityId;
    }

    public void setAppointmentAuthorityId(Long appointmentAuthorityId) {
        this.appointmentAuthorityId = appointmentAuthorityId;
    }

    public Long getAppointmentTypeId() {
        return appointmentTypeId;
    }

    public void setAppointmentTypeId(Long appointmentTypeId) {
        this.appointmentTypeId = appointmentTypeId;
    }

    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public Long getDesignationId() {
        return designationId;
    }

    public void setDesignationId(Long designationId) {
        this.designationId = designationId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Integer getProbationPeriod() {
        return probationPeriod;
    }

    public void setProbationPeriod(Integer probationPeriod) {
        this.probationPeriod = probationPeriod;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }
}