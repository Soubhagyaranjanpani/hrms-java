package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.ReasonCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReasonCategoryRepository extends JpaRepository<ReasonCategory,Long> {


    List<ReasonCategory> findById(ReasonCategory reasonCategory);

}
