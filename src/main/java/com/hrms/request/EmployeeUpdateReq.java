package com.hrms.request;



import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeUpdateReq {
    private Long id;
    private String name;
    private String phone;
    private Long roleId;
    private Long departmentId;
    private Long branchId;
    private String address;
    private String profilePicture;
    private Boolean isActive;
}
