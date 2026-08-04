package com.hrms.training.infrastructure;

import com.hrms.training.domain.TrainingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrainingRepository extends JpaRepository<TrainingRecord, Long> {

    Page<TrainingRecord> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        SELECT t FROM TrainingRecord t
        WHERE t.isDeleted = false AND (
            LOWER(t.trainingName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(t.provider) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(t.employee AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<TrainingRecord> searchByNameOrProviderOrEmployee(
            @Param("search") String search, Pageable pageable);

    Page<TrainingRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    List<TrainingRecord> findByIsActiveAndIsDeletedFalse(Boolean isActive);

    @Query("""
        SELECT t FROM TrainingRecord t
        WHERE t.isDeleted = false AND t.isActive = :isActive AND (
            LOWER(t.trainingName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(t.provider) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CAST(t.employee AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<TrainingRecord> searchByNameOrProviderOrEmployeeAndIsActive(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<TrainingRecord> findByEmployee_IdAndIsDeletedFalseOrderByStartDateDesc(Long empId);
}
