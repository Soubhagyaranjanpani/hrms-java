package com.hrms.serviceBook.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.serviceBook.domain.ServiceBook;
import com.hrms.serviceBook.dto.ServiceBookRequest;
import com.hrms.serviceBook.dto.ServiceBookResponse;
import com.hrms.serviceBook.infrastructure.ServiceBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceBookService {

    private final ServiceBookRepository repository;

    private final EmployeeRepository employeeRepository;

    @Value("${servicebook.prefix}")
    private String prefix;


    // ========================= CREATE =========================
    public ServiceBookResponse create(ServiceBookRequest request) {

        if (repository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee already exists");
        }

        Optional<Employee> byId = employeeRepository.findById(request.getEmployeeId());
        if(byId.isEmpty()){
            throw new RuntimeException("Invalid employee Id");
        }
        Employee employee = byId.get();


        ServiceBook entity = new ServiceBook();

        entity.setEmployee(employee);
        entity.setServiceBookName(request.getServiceName());


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
    public ServiceBookResponse update(Long id, String name) {

        ServiceBook entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Service Book not found"));



        entity.setServiceBookName(name);

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
//    private ServiceBookResponse mapToResponse(ServiceBook entity) {
//
//        ServiceBookResponse response = new ServiceBookResponse();
//
//        response.setId(entity.getId());
//        response.setEmployeeName(entity.getEmployee().getFullName());
//        response.setEmployeeCode(entity.getEmployee().getEmployeeCode());
//        response.setDepartment(entity.getEmployee().getDepartment().getName());
//        response.setServiceBookNo(entity.getServiceBookNo());
//        response.setServiceBookName(entity.getServiceBookName());
//        response.setIsActive(entity.getIsActive());
//
//        return response;
//    }


    private ServiceBookResponse mapToResponse(ServiceBook entity) {

        ServiceBookResponse response = new ServiceBookResponse();
        response.setId(entity.getId());
        response.setServiceBookNo(entity.getServiceBookNo());
        response.setServiceBookName(entity.getServiceBookName());
        response.setIsActive(entity.getIsActive());

        Employee employee = entity.getEmployee();
        if (employee != null) {
            response.setEmployeeName(employee.getFullName());
            response.setEmployeeCode(employee.getEmployeeCode());
            response.setDepartment(
                    employee.getDepartment() != null ? employee.getDepartment().getName() : null
            );
        }

        return response;
    }
}