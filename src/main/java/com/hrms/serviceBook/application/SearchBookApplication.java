package com.hrms.serviceBook.application;

import com.hrms.serviceBook.domain.SearchBook;
import com.hrms.serviceBook.dto.SearchBookRequest;
import com.hrms.serviceBook.dto.SearchBookResponse;
import com.hrms.serviceBook.infrastructure.SearchBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchBookApplication {

    private final SearchBookRepository repository;

    // CREATE
    public SearchBookResponse create(SearchBookRequest request) {

        SearchBook entity = new SearchBook();

        entity.setEmployeeName(request.getEmployeeName());
        entity.setDepartment(request.getDepartment());
        entity.setDesignation(request.getDesignation());
        entity.setStatus(request.getStatus());
        entity.setJoiningDate(request.getJoiningDate());
        entity.setRetirementDate(request.getRetirementDate());
        entity.setFlag(0);

        return map(repository.save(entity));
    }

    // GET ALL BY FLAG
    public List<SearchBookResponse> getAll(Integer flag) {

        return repository.findByFlag(flag)
                .stream()
                .map(this::map)
                .toList();
    }

    // GET BY ID
    public SearchBookResponse getById(Long id) {

        SearchBook entity = repository.findByIdAndFlag(id, 0)
                .orElseThrow(() ->
                        new RuntimeException("Search Book not found"));

        return map(entity);
    }

    // UPDATE
    public SearchBookResponse update(Long id,
                                     SearchBookRequest request) {

        SearchBook entity = repository.findByIdAndFlag(id, 0)
                .orElseThrow(() ->
                        new RuntimeException("Search Book not found"));

        entity.setEmployeeName(request.getEmployeeName());
        entity.setDepartment(request.getDepartment());
        entity.setDesignation(request.getDesignation());
        entity.setStatus(request.getStatus());
        entity.setJoiningDate(request.getJoiningDate());
        entity.setRetirementDate(request.getRetirementDate());

        return map(repository.save(entity));
    }

    // SOFT DELETE
    public void delete(Long id) {

        SearchBook entity = repository.findByIdAndFlag(id, 0)
                .orElseThrow(() ->
                        new RuntimeException("Search Book not found"));

        entity.setFlag(1);

        repository.save(entity);
    }

    // ENTITY TO RESPONSE
    private SearchBookResponse map(SearchBook entity) {

        SearchBookResponse response = new SearchBookResponse();

        response.setId(entity.getId());
        response.setEmployeeName(entity.getEmployeeName());
        response.setDepartment(entity.getDepartment());
        response.setDesignation(entity.getDesignation());
        response.setStatus(entity.getStatus());
        response.setJoiningDate(entity.getJoiningDate());
        response.setRetirementDate(entity.getRetirementDate());

        return response;
    }
}