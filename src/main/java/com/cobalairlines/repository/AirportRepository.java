package com.cobalairlines.repository;

import com.cobalairlines.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> {
    List<Airport> findByCity(String city);
    List<Airport> findByCountry(String country);
}
