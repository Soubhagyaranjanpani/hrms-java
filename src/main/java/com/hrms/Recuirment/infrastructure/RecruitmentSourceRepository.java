package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.RecruitmentSources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentSourceRepository extends JpaRepository<RecruitmentSources, Long> {
}
