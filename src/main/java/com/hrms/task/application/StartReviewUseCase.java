package com.hrms.task.application;



import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;


import com.hrms.task.domain.PerformanceReview;
import com.hrms.task.dto.PerformanceResponse;
import com.hrms.task.dto.StartReviewRequest;
import com.hrms.task.infrastructure.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StartReviewUseCase {

    private final PerformanceReviewRepository repo;
    private final EmployeeRepository employeeRepo;
    private final GetPerformanceListUseCase getPerformanceListUseCase;

    public PerformanceResponse execute(StartReviewRequest req) {

        Employee employee = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // derive status from rating
        String status = deriveStatus(req.getRating());

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setRating(req.getRating());
        review.setTotalGoals(req.getTotalGoals());
        review.setAchievedGoals(req.getAchievedGoals());
        review.setImprovementPercent(req.getImprovementPercent());
        review.setStatus(status);
        review.setReviewCycle(req.getReviewCycle());
        review.setReviewedAt(LocalDateTime.now());
        review.setCreatedAt(LocalDateTime.now());
        review.setIsDeleted(false);

        return getPerformanceListUseCase.map(repo.save(review));
    }

    private String deriveStatus(Double rating) {
        if (rating == null) return "Satisfactory";
        if (rating >= 4.8) return "Outstanding";
        if (rating >= 4.4) return "Excellent";
        if (rating >= 4.0) return "Great";
        if (rating >= 3.5) return "Good";
        return "Satisfactory";
    }
}
