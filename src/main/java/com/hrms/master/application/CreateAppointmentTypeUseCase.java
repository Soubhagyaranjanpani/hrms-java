package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.master.dto.AppointmentTypeCreateReq;

public interface CreateAppointmentTypeUseCase {

    ApiResponse<DefaultResponse> execute(AppointmentTypeCreateReq request);
}