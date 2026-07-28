package com.hrms.master.application;

import com.hrms.master.dto.RevisionReasonRequest;
import com.hrms.master.domain.RevisionReason;
import com.hrms.master.infrastructure.RevisionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevisionReasonService {

    private final RevisionReasonRepository repository;

    public List<RevisionReasonRequest> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RevisionReasonRequest create(RevisionReasonRequest dto) {
        if (repository.existsByNameIgnoreCase(dto.getName())) {
            throw new RuntimeException("Revision Reason already exists");
        }
        RevisionReason entity = new RevisionReason();
        entity.setName(dto.getName());
        entity.setIsActive(true);
        return toDTO(repository.save(entity));
    }

    public RevisionReasonRequest update(RevisionReasonRequest dto) {
        RevisionReason entity = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Revision Reason not found"));
        entity.setName(dto.getName());
        return toDTO(repository.save(entity));
    }

    public RevisionReasonRequest updateStatus(Long id) {
        RevisionReason entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Revision Reason not found"));
        entity.setIsActive(!entity.getIsActive());
        return toDTO(repository.save(entity));
    }

    private RevisionReasonRequest toDTO(RevisionReason entity) {
        RevisionReasonRequest dto = new RevisionReasonRequest();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }
}