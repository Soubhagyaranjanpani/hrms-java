package com.hrms.disciplinary.infrastructure;

import com.hrms.disciplinary.domain.DisciplinaryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DisciplinaryRepository extends JpaRepository<DisciplinaryRecord, Long> {

    Page<DisciplinaryRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT d FROM DisciplinaryRecord d
        WHERE d.isDeleted = false AND (
            LOWER(d.caseNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.employee.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.employee.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.actionType.name) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<DisciplinaryRecord> searchByCaseNumberOrEmployeeOrAction(
            @Param("search") String search, Pageable pageable);

    Page<DisciplinaryRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<DisciplinaryRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT d FROM DisciplinaryRecord d
        WHERE d.isDeleted = false AND d.isActive = :isActive AND (
            LOWER(d.caseNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.employee.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.employee.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(d.actionType.name) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<DisciplinaryRecord> searchByCaseNumberOrEmployeeOrActionAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<DisciplinaryRecord> findByEmployee_IdAndIsDeletedFalseOrderByIncidentDateDesc(Long empId);
    List<DisciplinaryRecord> findByEmployee_IdAndIsDeletedFalse(Long employeeId);
    List<DisciplinaryRecord> findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(Long employeeId);

}