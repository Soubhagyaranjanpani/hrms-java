package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRoundRepository extends JpaRepository<InterviewRound,Long> {

    List<InterviewRound> findByDepartmentId(Long id);
}
