package com.hrms.serviceBook.application;

import com.hrms.serviceBook.domain.ServiceBook;
import com.hrms.serviceBook.dto.ServiceBookRequest;
import com.hrms.serviceBook.dto.ServiceBookResponse;
import com.hrms.serviceBook.infrastructure.ServiceBookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceBookService {

    private final ServiceBookRepository repository;

    @Value("${servicebook.prefix}")
    private String prefix;

    public ServiceBookService(ServiceBookRepository repository) {
        this.repository = repository;
    }

    // ========================= CREATE =========================
    public ServiceBookResponse create(ServiceBookRequest request) {

        if (repository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Employee Code already exists");
        }

        ServiceBook entity = new ServiceBook();

        entity.setEmployeeName(request.getEmployeeName());
        entity.setEmployeeCode(request.getEmployeeCode());
        entity.setDepartment(request.getDepartment());
        entity.setDesignation(request.getDesignation());

        // Auto Generate Service Book Number
        entity.setServiceBookNo(generateServiceBookNo());

        entity.setIsActive(true);

        ServiceBook saved = repository.save(entity);

        return mapToResponse(saved);
    }

    // ========================= GET ALL =========================
    public List<ServiceBookResponse> getAll(Integer flag) {

        List<ServiceBook> records;

        if (flag != null && flag == 1) {
            records = repository.findByIsActiveTrueAndIsDeletedFalse();
        } else {
            records = repository.findByIsDeletedFalse();
        }

        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ========================= GET BY ID =========================
    public ServiceBookResponse getById(Long id) {

        ServiceBook entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Service Book not found"));

        return mapToResponse(entity);
    }

    // ========================= UPDATE =========================
    public ServiceBookResponse update(Long id, ServiceBookRequest request) {

        ServiceBook entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Service Book not found"));

        if (repository.existsByEmployeeCodeAndIdNot(request.getEmployeeCode(), id)) {
            throw new RuntimeException("Employee Code already exists");
        }

        entity.setEmployeeName(request.getEmployeeName());
        entity.setEmployeeCode(request.getEmployeeCode());
        entity.setDepartment(request.getDepartment());
        entity.setDesignation(request.getDesignation());

        // Service Book Number change nahi hoga

        ServiceBook updated = repository.save(entity);

        return mapToResponse(updated);
    }

    // ========================= DELETE =========================
    public void delete(Long id) {

        ServiceBook entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Service Book not found"));

        entity.setIsDeleted(true);
        entity.setIsActive(false);

        repository.save(entity);
    }

    // ========================= CHANGE STATUS =========================
    public ServiceBookResponse changeStatus(Long id) {

        ServiceBook entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Service Book not found"));

        entity.setIsActive(!entity.getIsActive());

        ServiceBook updated = repository.save(entity);

        return mapToResponse(updated);
    }

    // ========================= AUTO GENERATE SERVICE BOOK NO =========================
    private String generateServiceBookNo() {

        String lastNumber = repository.findLastServiceBookNo();

        if (lastNumber == null || lastNumber.isBlank()) {
            return prefix + "001";
        }

        String numericPart = lastNumber.substring(prefix.length());

        int next = Integer.parseInt(numericPart) + 1;

        return prefix + String.format("%03d", next);
    }

    // ========================= MAP RESPONSE =========================
    private ServiceBookResponse mapToResponse(ServiceBook entity) {

        ServiceBookResponse response = new ServiceBookResponse();

        response.setId(entity.getId());
        response.setEmployeeName(entity.getEmployeeName());
        response.setEmployeeCode(entity.getEmployeeCode());
        response.setDepartment(entity.getDepartment());
        response.setDesignation(entity.getDesignation());
        response.setServiceBookNo(entity.getServiceBookNo());
        response.setIsActive(entity.getIsActive());

        return response;
    }
}