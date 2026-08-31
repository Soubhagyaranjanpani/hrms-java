package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeUpdateReq {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String profilePicture;
    private LocalDate joiningDate;

    private Long roleId;
    private Long departmentId;
    private Long branchId;
    private Long managerId;
    private Long gradeId;
    private Long designationId;
    private Boolean isActive;
    private String bankAccount;
    private String uan;
    private String pan;

    // ── new: family / qualification / experience / nominee ──
    private String marriedStatus;
    private Integer childrenCount;
    private List<String> childrenNames;
    private String fatherName;
    private String motherName;
    private List<QualificationDto> qualifications;
    private List<ExperienceDto> experiences;
    private List<NomineeDto> nomineeList;
}