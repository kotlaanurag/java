package com.cobalairlines.repository;

import com.cobalairlines.entity.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, String> {
    List<Airplane> findByType(String type);
}
