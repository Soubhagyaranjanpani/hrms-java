//package com.hrms.appointment_type.application;
//
//import com.hrms.appointment_type.domain.Appointment_Type;
//import com.hrms.appointment_type.infrastructure.AppointmentAuthorityRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class AppointmentAuthorityService {
//
//    @Autowired
//    private AppointmentAuthorityRepository repository;
//
//    // Create
//    public Appointment_Type create(Appointment_Type authority) {
//        authority.setFlag(0);
//        return repository.save(authority);
//    }
//
//    // Get All By Flag
//    public List<Appointment_Type> getAllByFlag(int flag) {
//        return repository.findByFlag(flag);
//    }
//
//    // Get By Id
//    public Appointment_Type getById(Long id) {
//        return repository.findById(id)
//                .orElseThrow(() ->
//                        new RuntimeException("Authority not found with id: " + id));
//    }
//
//    // Update
//    public Appointment_Type update(Long id, Appointment_Type authority) {
//
//        Appointment_Type existing = getById(id);
//
//        existing.setAuthorityName(authority.getAuthorityName());
//        existing.setDepartment(authority.getDepartment());
//        existing.setEmail(authority.getEmail());
//
//        return repository.save(existing);
//    }
//
//    // Soft Delete
//    public void delete(Long id) {
//
//        Appointment_Type existing = getById(id);
//
//        existing.setFlag(1);
//
//        repository.save(existing);
//    }
//
//}
