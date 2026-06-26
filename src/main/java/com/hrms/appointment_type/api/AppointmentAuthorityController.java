package com.hrms.appointment_type.api;

import com.hrms.appointment_type.application.AppointmentAuthorityService;
import com.hrms.appointment_type.domain.AppointmentAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment-authority")
public class AppointmentAuthorityController {

    private final AppointmentAuthorityService service;

    public AppointmentAuthorityController(AppointmentAuthorityService service) {
        this.service = service;
    }

    @PostMapping
    public AppointmentAuthority create(@RequestBody AppointmentAuthority authority) {
        return service.create(authority);
    }

    @GetMapping
    public List<AppointmentAuthority> getAll(
            @RequestParam(defaultValue = "0") int flag) {

        return service.getAllByFlag(flag);
    }

    @GetMapping("/{id}")
    public AppointmentAuthority getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public AppointmentAuthority update(
            @PathVariable Long id,
            @RequestBody AppointmentAuthority authority) {

        return service.update(id, authority);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted Successfully";
    }
}