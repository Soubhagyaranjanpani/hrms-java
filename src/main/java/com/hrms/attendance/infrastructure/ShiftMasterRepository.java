package com.hrms.attendance.infrastructure;

import com.hrms.attendance.domain.ShiftMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftMasterRepository extends JpaRepository<ShiftMaster, Long> {

    List<ShiftMaster> findByIsActiveTrueOrderByIdAsc();

    Optional<ShiftMaster> findByCodeIgnoreCase(String code);

    Optional<ShiftMaster> findByCodeIgnoreCaseAndIsActiveTrue(String code);

    boolean existsByCodeIgnoreCase(String code);
}
