package com.cobalairlines.repository;

import com.cobalairlines.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    List<Shift> findByShiftDate(LocalDate date);
    List<Shift> findByCrewCrewId(Integer crewId);
}
