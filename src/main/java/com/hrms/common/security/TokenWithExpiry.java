package com.hrms.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenWithExpiry {
    private String token;
    private long expiryTime;
}
