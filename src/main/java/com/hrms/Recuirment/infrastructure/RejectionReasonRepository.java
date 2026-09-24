package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.RejectionReason;
import com.hrms.Recuirment.dto.RejectionReasonResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RejectionReasonRepository extends JpaRepository<RejectionReason,Long> {
    Optional<RejectionReason> findByReasonCode(String rejectionReason);

    Boolean existsByReasonCode(String rejectionReason);

    List<RejectionReasonResponse> findByReasonCategoryId(Long id);

}
