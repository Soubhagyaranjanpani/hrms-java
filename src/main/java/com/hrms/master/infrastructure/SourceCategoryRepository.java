package com.hrms.master.infrastructure;

import com.hrms.master.domain.SourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceCategoryRepository extends JpaRepository<SourceCategory, Long> {
}
