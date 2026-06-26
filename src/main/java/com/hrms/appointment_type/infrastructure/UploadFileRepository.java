package com.hrms.appointment_type.infrastructure;

import com.hrms.appointment_type.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
}