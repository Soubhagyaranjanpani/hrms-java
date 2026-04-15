package com.hrms.task.application;




import com.hrms.task.dto.TopPerformerResponse;
import com.hrms.task.infrastructure.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTopPerformersUseCase {

    private final PerformanceReviewRepository repo;

    public List<TopPerformerResponse> execute() {
        return repo.findTop3ByIsDeletedFalseOrderByRatingDesc()
                .stream()
                .map(p -> {
                    TopPerformerResponse r = new TopPerformerResponse();
                    r.setEmployeeId(p.getEmployee().getId());
                    r.setName(p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName());
                    r.setDepartment(p.getEmployee().getDepartment() != null
                            ? p.getEmployee().getDepartment().getName() : "");
                    r.setRating(p.getRating());
//                    r.setImprovementPercent(p.getImprovementPercent());
                    return r;
                })
                .toList();
    }
}
