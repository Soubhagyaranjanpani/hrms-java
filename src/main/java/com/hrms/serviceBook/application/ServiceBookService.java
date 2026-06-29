package com.hrms.serviceBook.application;

import com.hrms.serviceBook.domain.ServiceBook;
import com.hrms.serviceBook.dto.ServiceBookRequest;
import com.hrms.serviceBook.dto.ServiceBookResponse;
import com.hrms.serviceBook.infrastructure.ServiceBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceBookService {

    private final ServiceBookRepository repository;

    public ServiceBookService(ServiceBookRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public ServiceBookResponse create(ServiceBookRequest request) {

        ServiceBook entity = new ServiceBook();

        entity.setEmployeeName(request.getEmployeeName());
        entity.setEmployeeCode(request.getEmployeeCode());
        entity.setDepartment(request.getDepartment());
        entity.setDesignation(request.getDesignation());
        entity.setServiceBookNo(request.getServiceBookNo());

        // FIX: Naya record hamesha active (true) banega
        entity.setIsActive(true);

        ServiceBook saved = repository.save(entity);

        return mapToResponse(saved);
    }

    // FIX: Ab ye method flag accept karega aur filtered data dega
    public List<ServiceBookResponse> getAll(Integer flag) {
        List<ServiceBook> records;

        if (flag != null && flag == 1) {
            // flag = 1 -> Inactive/Deleted data
            records = repository.findByIsActiveFalse();
        } else {
            // flag = 0 -> Active data
            records = repository.findByIsActiveTrue();
        }

        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ServiceBookResponse getById(Long id) {

        ServiceBook entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Book not found"));

        return mapToResponse(entity);
    }

    public ServiceBookResponse update(Long id, ServiceBookRequest request) {

        ServiceBook entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Book not found"));

        entity.setEmployeeName(request.getEmployeeName());
        entity.setEmployeeCode(request.getEmployeeCode());
        entity.setDepartment(request.getDepartment());
        entity.setDesignation(request.getDesignation());
        entity.setServiceBookNo(request.getServiceBookNo());

        ServiceBook updated = repository.save(entity);

        return mapToResponse(updated);
    }

    // FIX: Hard delete ki jagah Soft Delete, taaki data flag=1 mein show ho sake
    public void delete(Long id) {
        ServiceBook entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Book not found"));

        entity.setIsActive(false);
        repository.save(entity);
    }

    private ServiceBookResponse mapToResponse(ServiceBook entity) {

        ServiceBookResponse response = new ServiceBookResponse();

        response.setId(entity.getId());
        response.setEmployeeName(entity.getEmployeeName());
        response.setEmployeeCode(entity.getEmployeeCode());
        response.setDepartment(entity.getDepartment());
        response.setDesignation(entity.getDesignation());
        response.setServiceBookNo(entity.getServiceBookNo());

        // Safe check taaki null value par crash na ho
        response.setIsActive(entity.getIsActive() != null ? entity.getIsActive() : false);

        return response;
    }
}