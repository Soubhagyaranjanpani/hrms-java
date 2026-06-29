package com.hrms.Appointment_Type.api;

import com.hrms.Appointment_Type.application.AppointmentTypeService;
import com.hrms.Appointment_Type.domain.Appointment_Type;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment-type")
public class AppointmentTypeController {

    private final AppointmentTypeService service;

    public AppointmentTypeController(AppointmentTypeService service) {
        this.service = service;
    }

    @PostMapping
    public Appointment_Type create(@RequestBody Appointment_Type appointmentType) {
        return service.create(appointmentType);
    }

    // ✅ GET ALL with flag filter (default 0 = active)
    @GetMapping
    public List<Appointment_Type> getAll(@RequestParam(defaultValue = "0") int flag) {
        return service.getAllByFlag(flag);
    }

    @GetMapping("/{id}")
    public Appointment_Type getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Appointment_Type update(
            @PathVariable Long id,
            @RequestBody Appointment_Type appointmentType) {

        return service.update(id, appointmentType);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted Successfully";
    }
}