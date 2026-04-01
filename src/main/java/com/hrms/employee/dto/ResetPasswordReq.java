package com.hrms.employee.dto;



import lombok.Data;

@Data
public class ResetPasswordReq {
    private String username;
    private String otp;
    private String newPassword;
    private String reEnterPassword;
}
