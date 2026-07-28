//package com.hrms.appointment_type.application;
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
//public class ChangeAppointmentTypeStatusUseCase {
//
//    private final AppointmentTypeRepository repository;
//
//    @Transactional
//    public ApiResponse<DefaultResponse> execute(Long id) {
//        AppointmentType entity = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Appointment type not found"));
//
//        if (entity.getIsDeleted()) {
//            throw new RuntimeException("Cannot change status of a deleted record");
//        }
//
//        entity.setIsActive(!entity.getIsActive());
//        entity.setUpdatedBy(getCurrentUser());
//        repository.save(entity);
//
//        String status = entity.getIsActive() ? "activated" : "deactivated";
//
//        // ✅ Use no-arg constructor and setter
//        DefaultResponse response = new DefaultResponse();
//        response.setMessage("Status " + status + " successfully");
//        return ApiResponse.success(response);
//    }
//
//    private String getCurrentUser() {
//        return "Admin";
//    }
//}