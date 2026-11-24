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

    public List<AttendancePolicy> getAll() {
        return repo.findAll();
    }

    public AttendancePolicy getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
    }

    @Transactional
    public AttendancePolicy update(Long id, AttendancePolicy req) {
        AttendancePolicy existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));

        if (Boolean.TRUE.equals(req.getIsActive())) {
            deactivateAllActivePolicies();
            existing.setIsActive(true);
        } else if (req.getIsActive() != null) {
            existing.setIsActive(req.getIsActive());
        }

        if (req.getName() != null) existing.setName(req.getName());
        if (req.getShiftStart() != null) existing.setShiftStart(req.getShiftStart());
        if (req.getShiftEnd() != null) existing.setShiftEnd(req.getShiftEnd());
        if (req.getGraceMinutes() != null) existing.setGraceMinutes(req.getGraceMinutes());
        if (req.getHalfDayThresholdHours() != null) existing.setHalfDayThresholdHours(req.getHalfDayThresholdHours());
        if (req.getFullDayHours() != null) existing.setFullDayHours(req.getFullDayHours());
        if (req.getAllowOvertime() != null) existing.setAllowOvertime(req.getAllowOvertime());
        if (req.getCheckInBeforeMinutes() != null) existing.setCheckInBeforeMinutes(req.getCheckInBeforeMinutes());
        if (req.getEnforceCheckOutAfterShift() != null) existing.setEnforceCheckOutAfterShift(req.getEnforceCheckOutAfterShift());
        if (req.getCheckOutGraceMinutes() != null) existing.setCheckOutGraceMinutes(req.getCheckOutGraceMinutes());
        if (req.getBreakMinutes() != null) existing.setBreakMinutes(req.getBreakMinutes());
        if (req.getIsNightShift() != null) existing.setIsNightShift(req.getIsNightShift());
        if (req.getShiftCode() != null) existing.setShiftCode(req.getShiftCode());
        if (req.getWeeklyOffPolicy() != null) existing.setWeeklyOffPolicy(req.getWeeklyOffPolicy());

        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        AttendancePolicy existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
        repo.delete(existing);
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