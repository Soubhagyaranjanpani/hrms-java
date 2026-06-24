package com.hrms.employee.application;

import com.hrms.employee.domain.EmployeeType;
import com.hrms.employee.infrastructure.EmployeeTypeRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeTypeService {


    private final EmployeeTypeRepository repository;


    public EmployeeTypeService(EmployeeTypeRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public EmployeeType create(EmployeeType employmentType) {
        return repository.save(employmentType);
    }

    // GET ALL WITH FLAG
    public List<EmployeeType> getAllByFlag(int flag) {

        if (flag == 0) {
            return repository.findByIsDeleted(false);
        }

        return repository.findByIsDeleted(true);
    }

    // GET BY ID
    public EmployeeType getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employment Type Not Found"));
    }

    // UPDATE
    public EmployeeType update(Long id, EmployeeType request) {

        EmployeeType entity = getById(id);

        entity.setEmploymentType(request.getEmploymentType());
        entity.setUpdatedBy(request.getUpdatedBy());

        return repository.save(entity);
    }

    // SOFT DELETE
    public void delete(Long id) {

        EmployeeType entity = getById(id);

        entity.setIsDeleted(true);

        repository.save(entity);
    }
}