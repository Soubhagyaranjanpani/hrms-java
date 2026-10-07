package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.JobLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobLocationRepository extends JpaRepository<JobLocation,Long> {

    Optional<JobLocation> findByLocationCode(String jobLocation);

    Boolean existsByLocationCode(String jobLocation);

}
