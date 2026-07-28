package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.master.dto.AppointmentTypeResponse;

import java.util.List;

public interface GetAppointmentTypeUseCase {

    ApiResponse<List<AppointmentTypeResponse>> execute(Integer flag);
}