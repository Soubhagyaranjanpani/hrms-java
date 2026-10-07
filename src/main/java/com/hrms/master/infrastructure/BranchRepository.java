package com.hrms.master.infrastructure;



import com.hrms.master.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByCode(String code);

    Optional<Branch> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<Branch> findByIsDeletedFalse();

    List<Branch> findByIsActiveTrueAndIsDeletedFalse();
}
