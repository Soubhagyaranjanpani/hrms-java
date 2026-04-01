package com.hrms.repository;



import com.hrms.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByName(String name);

    Optional<Branch> findByNameAndIsActiveTrue(String name);
}
