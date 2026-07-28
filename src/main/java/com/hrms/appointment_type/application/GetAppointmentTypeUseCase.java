//package com.hrms.appointment_type.application;
//
//
//import com.hrms.common.dto.response.ApiResponse;
//import com.hrms.appointment_type.dto.AppointmentTypeResponse;
//import com.hrms.appointment_type.domain.AppointmentType;
//import com.hrms.appointment_type.infrastructure.AppointmentTypeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class GetAppointmentTypeUseCase {
//
//    private final AppointmentTypeRepository repository;
//
//    public ApiResponse<List<AppointmentTypeResponse>> execute(Integer flag) {
//        List<AppointmentType> entities;
//        if (flag == 0) {
//            entities = repository.findAllByIsDeletedFalse(); // active only
//        } else {
//            entities = repository.findAll(); // all
//        }
//        List<AppointmentTypeResponse> responseList = entities.stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//        return ApiResponse.success(responseList);
//    }
//
//    private AppointmentTypeResponse toResponse(AppointmentType entity) {
//        AppointmentTypeResponse response = new AppointmentTypeResponse();
//        response.setId(entity.getId());
//        response.setAppointmentType(entity.getAppointmentType());
//        response.setIsActive(entity.getIsActive());
//        response.setIsDeleted(entity.getIsDeleted());
//        response.setCreatedBy(entity.getCreatedBy());
//        response.setCreatedAt(entity.getCreatedAt());
//        response.setUpdatedBy(entity.getUpdatedBy());
//        response.setUpdatedAt(entity.getUpdatedAt());
//        return response;
//    }
//}