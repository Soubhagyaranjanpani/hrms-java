package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeConfirmationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeConfirmationDocumentRepository
        extends JpaRepository<EmployeeConfirmationDocument, Long> {

    List<EmployeeConfirmationDocument> findByEmployeeConfirmationId(Long employeeConfirmationId);

    List<EmployeeConfirmationDocument> findByIsDeleted(Boolean isDeleted);
}