package com.hrms.request;



import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeCreationReq {
    private String email;
    private String password;
    private String name;
    private String phone;
    private LocalDate joiningDate;
    private Long roleId;
    private Long departmentId;
    private Long branchId;
    private String address;
    private String profilePicture;
}
