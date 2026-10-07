package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.AppointmentType;
import com.hrms.master.dto.AppointmentTypeResponse;
import com.hrms.master.infrastructure.AppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAppointmentTypeUseCase {

    private final AppointmentTypeRepository appointmentTypeRepository;

    public ApiResponse<List<AppointmentTypeResponse>> execute(Integer flag) {

        List<AppointmentType> list;

        if (flag == 1) {
            list = appointmentTypeRepository.findByIsActiveTrue();
        } else {
            list = appointmentTypeRepository.findAll();
        }

        List<AppointmentTypeResponse> response = list.stream().map(a -> {
            AppointmentTypeResponse res = new AppointmentTypeResponse();
            res.setId(a.getId());
            res.setAppointmentType(a.getAppointmentType());
            res.setIsActive(a.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}