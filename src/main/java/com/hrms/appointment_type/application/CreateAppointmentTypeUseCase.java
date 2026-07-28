//package com.hrms.appointment_type.application;   // your current package
//
//import com.hrms.common.dto.response.ApiResponse;
//import com.hrms.common.security.DefaultResponse;
//import com.hrms.appointment_type.dto.AppointmentTypeCreateReq;
//import com.hrms.appointment_type.domain.AppointmentType;
//import com.hrms.appointment_type.infrastructure.AppointmentTypeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class CreateAppointmentTypeUseCase {
//
//    private final AppointmentTypeRepository repository;
//
//    @Transactional
//    public ApiResponse<DefaultResponse> execute(AppointmentTypeCreateReq request) {
//        AppointmentType entity = AppointmentType.builder()
//                .appointmentType(request.getAppointmentType())
//                .isActive(true)
//                .isDeleted(false)
//                .createdBy(getCurrentUser())
//                .updatedBy(getCurrentUser())
//                .build();
//        repository.save(entity);
//        return ApiResponse.success(new DefaultResponse("Appointment type created successfully"));
//    }
//
//    private String getCurrentUser() {
//        // Replace with actual SecurityContext user
//        return "Admin";
//    }
//}