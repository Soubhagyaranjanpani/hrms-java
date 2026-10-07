package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.AppointmentType;
import com.hrms.master.infrastructure.AppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeAppointmentTypeStatusUseCase {

    private final AppointmentTypeRepository appointmentTypeRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        AppointmentType appointmentType = appointmentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment Type not found"));

        appointmentType.setIsActive(!appointmentType.getIsActive());

        appointmentTypeRepository.save(appointmentType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Appointment Type Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}