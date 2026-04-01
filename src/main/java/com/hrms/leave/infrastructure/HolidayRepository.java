package com.hrms.leave.infrastructure;

import com.hrms.leave.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByDateBetween(LocalDate start, LocalDate end);
}
