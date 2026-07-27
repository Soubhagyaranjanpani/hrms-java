package com.hrms.payrevision.infrastructure;

import com.hrms.payrevision.domain.PayRevisionReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayRevisionReasonRepository extends JpaRepository<PayRevisionReason, Long> {
    List<PayRevisionReason> findByIsActiveTrue();
}
