package com.hrms.serviceBook.api;

import com.hrms.serviceBook.application.ServiceBookService;
import com.hrms.serviceBook.dto.ServiceBookRequest;
import com.hrms.serviceBook.dto.ServiceBookResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-books")
public class ServiceBookController {

    private final ServiceBookService service;

    public ServiceBookController(ServiceBookService service) {
        this.service = service;
    }

    @PostMapping
    public ServiceBookResponse create(@RequestBody ServiceBookRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ServiceBookResponse> getAll(
            @RequestParam(name = "flag", defaultValue = "0") Integer flag) {
        return service.getAll(flag);
    }

    @GetMapping("/{id}")
    public ServiceBookResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/{name}")
    public ServiceBookResponse update(
            @PathVariable Long id,
            @PathVariable String name) {
        return service.update(id, name);
    }

    // NEW: Status Change API
    @PutMapping("/status/{id}")
    public ServiceBookResponse changeStatus(@PathVariable Long id) {
        return service.changeStatus(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Service Book Deleted Successfully";
    }
}