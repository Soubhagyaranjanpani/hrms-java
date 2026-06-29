package com.hrms.serviceBook.infrastructure;

import com.hrms.serviceBook.domain.ServiceBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceBookRepository
        extends JpaRepository<ServiceBook, Long> {

    // flag = 0 (Active data)
    List<ServiceBook> findByIsActiveTrue();

    // flag = 1 (Inactive/Deleted data)
    List<ServiceBook> findByIsActiveFalse();
}