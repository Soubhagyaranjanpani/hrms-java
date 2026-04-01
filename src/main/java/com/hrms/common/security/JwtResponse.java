package com.hrms.common.security;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String jwtToken;
    private String refreshToken;
    private String username;
    private Long employeeId;
    private String name;
    private String email;
    private Long roleId;
    private String roleName;
    private Long jwtTokenExpiry;
    private Long refreshTokenExpiry;
}
