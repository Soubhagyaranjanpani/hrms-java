package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.InterviewPanel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewPanelRepository extends JpaRepository<InterviewPanel,Long> {

    Optional<InterviewPanel> findByPanelCode(String panelCode);

    boolean existsByPanelCode(String panelCode);


}
