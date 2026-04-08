package com.hrms.leave.infrastructure;

import com.hrms.leave.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByDateBetween(LocalDate start, LocalDate end);

    Optional<Holiday> findByDateAndBranchIsNull(LocalDate date);
}
