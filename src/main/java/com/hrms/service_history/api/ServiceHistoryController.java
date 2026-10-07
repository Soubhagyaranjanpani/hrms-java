//package com.hrms.service_history.api;
//
//import com.hrms.service_history.appplication.ServiceHistoryService;
//import com.hrms.service_history.domain.ServiceHistory;
//import com.hrms.service_history.dto.ServiceHistoryRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//        import java.util.List;
////
//@RestController
//@RequestMapping("/api/service-history")
//public class ServiceHistoryController {
//
//    @Autowired
//    private ServiceHistoryService serviceHistoryService;
//
//    @PostMapping
//    public ServiceHistory addHistory(@RequestBody ServiceHistoryRequest request) {
//        return serviceHistoryService.addHistory(request);
//    }
//
//    @GetMapping("/service-book/{serviceBookId}")
//    public List<ServiceHistory> getByServiceBook(@PathVariable Long serviceBookId) {
//        return serviceHistoryService.getHistoryByServiceBook(serviceBookId);
//    }
//
//    @GetMapping("/employee/{employeeId}")
//    public List<ServiceHistory> getByEmployee(@PathVariable Long employeeId) {
//        return serviceHistoryService.getHistoryByEmployee(employeeId);
//    }
//}