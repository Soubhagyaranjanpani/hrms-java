package com.hrms.appointment.infrastructure;

import com.hrms.appointment.domain.AppointmentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentRecord, Long> {

    Page<AppointmentRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT a FROM AppointmentRecord a
        WHERE a.isDeleted = false AND (
            LOWER(a.appointmentOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(a.initialDesignation AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(a.initialDepartment AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<AppointmentRecord> searchByOrderNumberOrDesignationOrDepartment(
            @Param("search") String search, Pageable pageable);

    // ── Active/Inactive filtering (mirrors the promotion "flag" endpoints) ──
    Page<AppointmentRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<AppointmentRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT a FROM AppointmentRecord a
        WHERE a.isDeleted = false AND a.isActive = :isActive AND (
            LOWER(a.appointmentOrderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(a.initialDesignation AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(a.initialDepartment AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<AppointmentRecord> searchByOrderNumberOrDesignationOrDepartmentAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<AppointmentRecord> findByEmployee_IdAndIsDeletedFalseOrderByAppointmentDateDesc(Long empId);
    List<AppointmentRecord> findByEmployee_IdAndIsDeletedFalse(Long employeeId);
    List<AppointmentRecord> findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(Long employeeId);


}
