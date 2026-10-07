package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.security.JwtHelper;
import com.hrms.common.security.TokenBlacklistService;
import com.hrms.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final JwtHelper jwtHelper;
    private final TokenBlacklistService tokenBlacklistService;

    public ApiResponse<DefaultResponse> execute(HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);

        long expiry = jwtHelper.getExpirationTime(token);
        tokenBlacklistService.addToBlacklist(token, expiry);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Logged out");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
