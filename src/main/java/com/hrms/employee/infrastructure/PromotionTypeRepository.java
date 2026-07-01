package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.PromotionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionTypeRepository extends JpaRepository<PromotionType, Long> {

    List<PromotionType> findByIsDeletedFalse();

    Optional<PromotionType> findByIdAndIsDeletedFalse(Long id);

    List<PromotionType> findByStatusAndIsDeletedFalse(Boolean status);

    boolean existsByPromotionTypeNameIgnoreCaseAndIsDeletedFalse(String promotionTypeName);

    boolean existsByPromotionTypeNameIgnoreCaseAndIdNotAndIsDeletedFalse(
            String promotionTypeName,
            Long id
    );
}