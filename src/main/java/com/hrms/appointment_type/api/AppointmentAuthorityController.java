//package com.hrms.appointment_type.api;
//
//import com.hrms.appointment_type.application.AppointmentAuthorityService;
//import com.hrms.appointment_type.domain.Appointment_Type;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/appointment-authority")
//public class AppointmentAuthorityController {
//
//    private final AppointmentAuthorityService service;
//
//    public AppointmentAuthorityController(AppointmentAuthorityService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    public Appointment_Type create(@RequestBody Appointment_Type authority) {
//        return service.create(authority);
//    }
//
//    @GetMapping
//    public List<Appointment_Type> getAll(
//            @RequestParam(defaultValue = "0") int flag) {
//
//        return service.getAllByFlag(flag);
//    }
//
//    @GetMapping("/{id}")
//    public Appointment_Type getById(@PathVariable Long id) {
//        return service.getById(id);
//    }
//
//    @PutMapping("/{id}")
//    public Appointment_Type update(
//            @PathVariable Long id,
//            @RequestBody Appointment_Type authority) {
//
//        return service.update(id, authority);
//    }
//
//    @DeleteMapping("/{id}")
//    public String delete(@PathVariable Long id) {
//        service.delete(id);
//        return "Deleted Successfully";
//    }
//}