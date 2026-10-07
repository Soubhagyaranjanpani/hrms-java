package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.StatusCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusCategoryRepository extends JpaRepository<StatusCategory,Long> {

}
