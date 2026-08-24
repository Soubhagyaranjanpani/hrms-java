package com.hrms.service_history.infrastructure;


import com.hrms.service_history.domain.ServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long> {

//    List<ServiceHistory> findByServiceBook_Id(Long serviceBookId);
//
//    List<ServiceHistory> findByEmployee_Id(Long employeeId);
}