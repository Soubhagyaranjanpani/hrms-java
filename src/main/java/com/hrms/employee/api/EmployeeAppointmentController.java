package com.hrms.employee.api;

import com.hrms.employee.application.EmployeeAppointmentService;
import com.hrms.employee.domain.EmployeeAppointment;
import com.hrms.employee.dto.EmployeeAppointmentRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-appointments")
public class EmployeeAppointmentController {

    private final EmployeeAppointmentService service;

    public EmployeeAppointmentController(EmployeeAppointmentService service) {
        this.service = service;
    }

    // Create
    @PostMapping
    public EmployeeAppointment create(@RequestBody EmployeeAppointmentRequest request) {
        return service.save(request);
    }

    // Get All
    @GetMapping
    public List<EmployeeAppointment> getAll(
            @RequestParam(defaultValue = "0") int flag) {

        return service.getAllByFlag(flag);
    }

    // Get By Id
    @GetMapping("/{id}")
    public EmployeeAppointment getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // Update
    @PutMapping("/{id}")
    public EmployeeAppointment update(@PathVariable Long id,
                                      @RequestBody EmployeeAppointmentRequest request) {
        return service.update(id, request);
    }

    // Delete
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted Successfully";
    }
}