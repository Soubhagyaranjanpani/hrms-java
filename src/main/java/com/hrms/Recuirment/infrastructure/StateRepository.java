package com.hrms.Recuirment.infrastructure;

import com.hrms.Recuirment.domain.State;
import com.hrms.Recuirment.dto.StateResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State,Long> {

    List<State> findByCountryId(Long id);

}
