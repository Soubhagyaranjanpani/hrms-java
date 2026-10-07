package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.AppointmentType;
import com.hrms.master.dto.AppointmentTypeCreateReq;
import com.hrms.master.infrastructure.AppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAppointmentTypeUseCase {

    private final AppointmentTypeRepository appointmentTypeRepository;

    public ApiResponse<DefaultResponse> execute(AppointmentTypeCreateReq request) {

        if (appointmentTypeRepository.existsByAppointmentType(request.getAppointmentType())) {
            throw new RuntimeException("Appointment Type already exists");
        }

        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setAppointmentType(request.getAppointmentType());

        appointmentTypeRepository.save(appointmentType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Appointment Type Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}