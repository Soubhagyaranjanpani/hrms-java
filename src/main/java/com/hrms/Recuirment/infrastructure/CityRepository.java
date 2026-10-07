package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.City;
import com.hrms.Recuirment.domain.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City,Long> {
    List<City> findByStateId(Long id);

}
