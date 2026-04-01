package com.hrms.request;



import lombok.Data;

@Data
public class PasswordChangeReq {
    private String username;
    private String currentPassword;
    private String newPassword;
}
