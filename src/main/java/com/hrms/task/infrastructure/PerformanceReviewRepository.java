package com.hrms.task.infrastructure;



import com.hrms.task.domain.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {

    List<PerformanceReview> findByIsDeletedFalse();

    List<PerformanceReview> findByReviewCycleAndIsDeletedFalse(String reviewCycle);

    // top performers by rating descending
    List<PerformanceReview> findTop3ByIsDeletedFalseOrderByRatingDesc();

    // for stats: avg rating
    @Query("SELECT AVG(p.rating) FROM PerformanceReview p WHERE p.isDeleted = false")
    Double findAvgRating();

    // employees with rating >= threshold
    @Query("SELECT COUNT(p) FROM PerformanceReview p WHERE p.rating >= :threshold AND p.isDeleted = false")
    Integer countByRatingGreaterThanEqual(@Param("threshold") Double threshold);

    // total achieved vs total goals across all reviews
    @Query("SELECT SUM(p.achievedGoals), SUM(p.totalGoals) FROM PerformanceReview p WHERE p.isDeleted = false")
    Object[] sumGoals();
}
