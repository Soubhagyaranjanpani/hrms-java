package com.hrms.employee.api;

import com.hrms.employee.application.PromotionTypeService;
import com.hrms.employee.dto.PromotionTypeRequest;
import com.hrms.employee.dto.PromotionTypeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion-types")
public class PromotionTypeController {

    private final PromotionTypeService service;

    public PromotionTypeController(PromotionTypeService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public PromotionTypeResponse create(@RequestBody PromotionTypeRequest request) {
        return service.save(request);
    }

    // GET ALL (FLAG BASED)
    @GetMapping
    public List<PromotionTypeResponse> getAll(
            @RequestParam(defaultValue = "0") int flag) {

        return service.getAllByFlag(flag);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public PromotionTypeResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public PromotionTypeResponse update(
            @PathVariable Long id,
            @RequestBody PromotionTypeRequest request) {

        return service.update(id, request);
    }

    // DELETE (SOFT DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted Successfully");
    }
}