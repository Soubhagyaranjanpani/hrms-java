package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.AppointmentType;
import com.hrms.master.dto.AppointmentTypeUpdateReq;
import com.hrms.master.infrastructure.AppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAppointmentTypeUseCase {

    private final AppointmentTypeRepository appointmentTypeRepository;

    public ApiResponse<DefaultResponse> execute(AppointmentTypeUpdateReq request) {

        AppointmentType appointmentType = appointmentTypeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Appointment Type not found"));

        appointmentType.setAppointmentType(request.getAppointmentType());

        appointmentTypeRepository.save(appointmentType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Appointment Type Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}