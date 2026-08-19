package com.hrms.Employee_Service_History.api;

//import com.hrms.searchBook.application.EmployeeServiceHistoryService;
import com.hrms.Employee_Service_History.dto.EmployeeServiceHistoryDTO;
import com.hrms.Employee_Service_History.service.EmployeeServiceHistoryService;
//import com.hrms.dto.EmployeeServiceHistoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-books")
@RequiredArgsConstructor
public class EmployeeServiceHistoryController {

    private final EmployeeServiceHistoryService historyService;

    @GetMapping("/employees/{employeeId}/history")
    public ResponseEntity<EmployeeServiceHistoryDTO> getHistory(@PathVariable Long employeeId) {
        return ResponseEntity.ok(historyService.getHistory(employeeId));
    }
}