package com.hrms.serviceBook.api;


import com.hrms.serviceBook.application.SearchBookApplication;
import com.hrms.serviceBook.dto.SearchBookRequest;
import com.hrms.serviceBook.dto.SearchBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search-book")
@RequiredArgsConstructor
public class SearchBookController {

    private final SearchBookApplication application;

    @PostMapping
    public SearchBookResponse create(
            @RequestBody SearchBookRequest request) {

        return application.create(request);
    }

    @GetMapping
    public List<SearchBookResponse> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {

        return application.getAll(flag);
    }

    @GetMapping("/{id}")
    public SearchBookResponse getById(
            @PathVariable Long id) {

        return application.getById(id);
    }

    @PutMapping("/{id}")
    public SearchBookResponse update(
            @PathVariable Long id,
            @RequestBody SearchBookRequest request) {

        return application.update(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id) {

        application.delete(id);

        return "Search Book Deleted Successfully";
    }

}