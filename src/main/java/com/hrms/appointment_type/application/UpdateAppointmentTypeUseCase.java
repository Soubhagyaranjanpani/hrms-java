//package com.hrms.appointment_type.application;
//
//
//import com.hrms.common.dto.response.ApiResponse;
//import com.hrms.common.security.DefaultResponse;
//import com.hrms.appointment_type.domain.AppointmentType;
//import com.hrms.appointment_type.infrastructure.AppointmentTypeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class UpdateAppointmentTypeUseCase {
//
//    private final AppointmentTypeRepository repository;
//
//    @Transactional
//    public ApiResponse<DefaultResponse> execute(AppointmentTypeUpdateReq request) {
//        AppointmentType entity = repository.findById(request.getId())
//                .orElseThrow(() -> new RuntimeException("Appointment type not found"));
//        if (entity.getIsDeleted()) {
//            return ApiResponse.error("Cannot update a deleted record");
//        }
//        entity.setAppointmentType(request.getAppointmentType());
//        entity.setUpdatedBy(getCurrentUser());
//        repository.save(entity);
//        return ApiResponse.success(new DefaultResponse("Appointment type updated successfully"));
//    }
//
//    private String getCurrentUser() {
//        return "Admin";
//    }
//}