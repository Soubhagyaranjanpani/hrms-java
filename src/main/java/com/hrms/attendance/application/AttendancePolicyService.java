package com.hrms.attendance.application;

import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendancePolicyService {

    private final AttendancePolicyRepository repo;

    @Transactional
    public AttendancePolicy save(AttendancePolicy policy) {
        if (Boolean.TRUE.equals(policy.getIsActive())) {
            // Deactivate all active policies before saving new active one
            deactivateAllActivePolicies();
        }
        return repo.save(policy);
    }

    public AttendancePolicy getActive() {
        // FIXED: Get list instead of single Optional
        List<AttendancePolicy> activePolicies = repo.findByIsActiveTrue();

        if (activePolicies.isEmpty()) {
            throw new RuntimeException("No active policy");
        }

        // If multiple active policies exist, return the latest one
        if (activePolicies.size() > 1) {
            // Fix the inconsistency by keeping only the latest
            AttendancePolicy latest = activePolicies.stream()
                    .max((p1, p2) -> p1.getId().compareTo(p2.getId()))
                    .orElse(activePolicies.get(0));

            // Deactivate others
            activePolicies.stream()
                    .filter(p -> !p.getId().equals(latest.getId()))
                    .forEach(p -> {
                        p.setIsActive(false);
                        repo.save(p);
                    });

            return latest;
        }

        return activePolicies.get(0);
    }

    @Transactional
    public void activate(Long id) {
        // Deactivate all active policies
        deactivateAllActivePolicies();

        // Activate the selected one
        AttendancePolicy p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        p.setIsActive(true);
        repo.save(p);
    }

    private void deactivateAllActivePolicies() {
        List<AttendancePolicy> activePolicies = repo.findByIsActiveTrue();
        activePolicies.forEach(p -> {
            p.setIsActive(false);
            repo.save(p);
        });
    }
}