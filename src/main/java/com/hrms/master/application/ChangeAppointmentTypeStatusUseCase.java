package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;

public interface ChangeAppointmentTypeStatusUseCase {

    ApiResponse<DefaultResponse> execute(Long id);
}