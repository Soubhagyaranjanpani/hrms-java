package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateStatusRepository extends JpaRepository<CandidateStatus,Long> {

    Optional<CandidateStatus> findByStatusCode(String jobLocation);

    Boolean existsByStatusCode(String jobLocation);
}
