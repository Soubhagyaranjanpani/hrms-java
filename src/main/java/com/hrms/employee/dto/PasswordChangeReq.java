package com.hrms.employee.dto;



import lombok.Data;

@Data
public class PasswordChangeReq {
    private String username;
    private String currentPassword;
    private String newPassword;
}
