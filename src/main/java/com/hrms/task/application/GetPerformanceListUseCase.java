package com.hrms.task.application;






import com.hrms.task.domain.PerformanceReview;
import com.hrms.task.dto.PerformanceResponse;
import com.hrms.task.infrastructure.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPerformanceListUseCase {

    private final PerformanceReviewRepository repo;

    public List<PerformanceResponse> execute() {
        return repo.findByIsDeletedFalse()
                .stream()
                .map(this::map)
                .toList();
    }

    PerformanceResponse map(PerformanceReview p) {
        PerformanceResponse r = new PerformanceResponse();
        r.setId(p.getId());
        r.setEmployeeId(p.getEmployee().getId());
        r.setName(p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName());
        r.setDepartment(p.getEmployee().getDepartment() != null
                ? p.getEmployee().getDepartment().getName() : "");
        r.setRating(p.getRating());
        r.setTotalGoals(p.getTotalGoals());
        r.setAchievedGoals(p.getAchievedGoals());
        r.setImprovementPercent(p.getImprovementPercent());
        r.setStatus(p.getStatus());
        r.setReviewCycle(p.getReviewCycle());
        return r;
    }
}
