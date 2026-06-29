package com.hrms.serviceBook.infrastructure;

import com.hrms.serviceBook.domain.SearchBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchBookRepository extends JpaRepository<SearchBook, Long> {

    List<SearchBook> findByFlag(Integer flag);

    Optional<SearchBook> findByIdAndFlag(Long id, Integer flag);
}