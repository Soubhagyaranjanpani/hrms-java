package com.hrms.response;



import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String roleName;
    private String departmentName;
    private String branchName;
    private LocalDate joiningDate;
    private String address;
    private String profilePicture;
    private LocalDateTime lastLogin;
}
