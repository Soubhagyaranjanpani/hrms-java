package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.InterviewType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewTypeRepository extends JpaRepository<InterviewType,Long> {

    List<InterviewType> findByInterviewModeId(Long id);

}
