package com.hrms.serviceBook.infrastructure;

import com.hrms.serviceBook.domain.ServiceBook;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceBookRepository extends JpaRepository<ServiceBook, Long> {

//    boolean existsByEmployeeCode(String employeeCode);
//
//    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);

    List<ServiceBook> findByIsDeletedFalse();

    List<ServiceBook> findByIsActiveTrueAndIsDeletedFalse();

    Optional<ServiceBook> findByIdAndIsDeletedFalse(Long id);

    @Query(value = "SELECT service_book_no FROM service_book ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastServiceBookNo();

    boolean existsByEmployeeId(@NotNull(message = "employeeId is required") Long employeeId);

    Optional<ServiceBook> findByEmployeeIdAndIsDeletedFalse(Long id);


}