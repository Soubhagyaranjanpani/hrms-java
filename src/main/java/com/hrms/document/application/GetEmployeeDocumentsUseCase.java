package com.hrms.document.application;

import com.hrms.document.dto.DocumentResponse;
import com.hrms.document.infrastructure.EmployeeDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetEmployeeDocumentsUseCase {

    private final EmployeeDocumentRepository repo;

    public Object execute(Long employeeId) {

        return repo.findByEmployee_Id(employeeId)
                .stream()
                .map(doc -> {
                    DocumentResponse d = new DocumentResponse();
                    d.setId(doc.getId());
                    d.setFileName(doc.getFileName());
                    d.setCategory(doc.getCategory().name());
                    d.setDocumentType(doc.getDocumentType());
                    return d;
                }).collect(Collectors.toList());
    }
}
