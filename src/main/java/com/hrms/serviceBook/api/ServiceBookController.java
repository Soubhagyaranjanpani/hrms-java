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

    // CREATE
    @PostMapping
    public ServiceBookResponse create(@RequestBody ServiceBookRequest request) {
        return service.create(request);
    }

    // GET ALL (Fixed: Ab ye 0 aur 1 flag accept karega)
    @GetMapping
    public List<ServiceBookResponse> getAll(
            @RequestParam(name = "flag", defaultValue = "0") Integer flag) {
        return service.getAll(flag);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ServiceBookResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ServiceBookResponse update(
            @PathVariable Long id,
            @RequestBody ServiceBookRequest request) {
        return service.update(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Service Book Deleted Successfully";
    }
}