package com.hrms.employee.dto.request;



import lombok.Data;

@Data
public class PasswordChangeReq {
    private String username;
    private String currentPassword;
    private String newPassword;
}
