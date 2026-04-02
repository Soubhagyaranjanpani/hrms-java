package com.hrms.attendance.application;

import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendancePolicyService {

    private final AttendancePolicyRepository repo;

    public AttendancePolicy save(AttendancePolicy policy) {
        return repo.save(policy);
    }

    public AttendancePolicy getActive() {
        return repo.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active policy"));
    }

    public void activate(Long id) {

        repo.findAll().forEach(p -> {
            p.setIsActive(false);
            repo.save(p);
        });

        AttendancePolicy p = repo.findById(id).orElseThrow();
        p.setIsActive(true);
        repo.save(p);
    }
}
