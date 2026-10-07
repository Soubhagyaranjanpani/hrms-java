package com.hrms.document.infrastructure;

import com.hrms.document.domain.EmployeeDocumentData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDocumentDataRepository extends JpaRepository<EmployeeDocumentData, Long> {
}
