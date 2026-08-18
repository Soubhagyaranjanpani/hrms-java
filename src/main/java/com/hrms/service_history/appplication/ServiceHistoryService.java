//package com.hrms.service_history.appplication;
//
//import com.hrms.employee.domain.Employee;
//import com.hrms.employee.infrastructure.EmployeeRepository;
//import com.hrms.serviceBook.domain.ServiceBook;
//
//import com.hrms.serviceBook.infrastructure.ServiceBookRepository;
//import com.hrms.service_history.domain.ServiceHistory;
//import com.hrms.service_history.infrastructure.ServiceHistoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ServiceHistoryService {
//
//    @Autowired
//    private ServiceHistoryRepository serviceHistoryRepository;
//
//    @Autowired
//    private ServiceBookRepository serviceBookRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    public ServiceHistory addHistory(ServiceHistoryRequest request) {
//        ServiceBook serviceBook = serviceBookRepository.findById(request.getServiceBookId())
//                .orElseThrow(() -> new RuntimeException("Service Book not found"));
//
//        Employee employee = employeeRepository.findById(request.getEmployeeId())
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//        ServiceHistory history = new ServiceHistory();
//        history.setServiceBook(serviceBook);
//        history.setEmployee(employee);
//        history.setEventType(request.getEventType());
//        history.setFromDesignation(request.getFromDesignation());
//        history.setToDesignation(request.getToDesignation());
//        history.setFromBranch(request.getFromBranch());
//        history.setToBranch(request.getToBranch());
//        history.setEventDate(request.getEventDate());
//        history.setRemarks(request.getRemarks());
//
//        return serviceHistoryRepository.save(history);
//    }
//
//    public List<ServiceHistory> getHistoryByServiceBook(Long serviceBookId) {
//        return serviceHistoryRepository.findByServiceBook_Id(serviceBookId);
//    }
//
//    public List<ServiceHistory> getHistoryByEmployee(Long employeeId) {
//        return serviceHistoryRepository.findByEmployee_Id(employeeId);
//    }
//}