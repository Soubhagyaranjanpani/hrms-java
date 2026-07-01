package com.hrms.employee.application;

import com.hrms.employee.domain.PromotionType;
import com.hrms.employee.dto.PromotionTypeRequest;
import com.hrms.employee.dto.PromotionTypeResponse;
import com.hrms.employee.infrastructure.PromotionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionTypeService {

    private final PromotionTypeRepository repository;

    // CREATE
    public PromotionTypeResponse save(PromotionTypeRequest request) {

        if (repository.existsByPromotionTypeNameIgnoreCaseAndIsDeletedFalse(
                request.getPromotionTypeName())) {
            throw new RuntimeException("Promotion Type already exists.");
        }

        PromotionType promotionType = PromotionType.builder()
                .promotionTypeName(request.getPromotionTypeName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : true)
                .isDeleted(false)
                .build();

        repository.save(promotionType);

        return mapToResponse(promotionType);
    }

    // GET ALL
// flag = 0 → ALL DATA (NOT DELETED)
    public List<PromotionTypeResponse> getAllByFlag(int flag) {

        if (flag == 0) {

            return repository.findByIsDeletedFalse()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        throw new RuntimeException("Invalid flag. Only 0 is allowed for GET ALL.");
    }


    // GET BY ID
    public PromotionTypeResponse getById(Long id) {

        PromotionType promotionType = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Promotion Type Not Found"));

        return mapToResponse(promotionType);
    }

    // UPDATE
    public PromotionTypeResponse update(Long id, PromotionTypeRequest request) {

        PromotionType promotionType = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Promotion Type Not Found"));

        if (repository.existsByPromotionTypeNameIgnoreCaseAndIdNotAndIsDeletedFalse(
                request.getPromotionTypeName(), id)) {
            throw new RuntimeException("Promotion Type already exists.");
        }

        promotionType.setPromotionTypeName(request.getPromotionTypeName());
        promotionType.setDescription(request.getDescription());
        promotionType.setStatus(
                request.getStatus() != null ? request.getStatus() : true
        );

        repository.save(promotionType);

        return mapToResponse(promotionType);
    }

    // SOFT DELETE
    public void delete(Long id) {

        PromotionType promotionType = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Promotion Type Not Found"));

        promotionType.setIsDeleted(true);

        repository.save(promotionType);
    }

    // MAPPER
    private PromotionTypeResponse mapToResponse(PromotionType promotionType) {

        return PromotionTypeResponse.builder()
                .id(promotionType.getId())
                .promotionTypeName(promotionType.getPromotionTypeName())
                .description(promotionType.getDescription())
                .status(promotionType.getStatus())
                .build();
    }
}