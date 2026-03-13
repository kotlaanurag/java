package com.cobalairlines.controller;

import com.cobalairlines.entity.Airport;
import com.cobalairlines.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Airport reference data controller.
 * Data seeded from DB2 insertion-2 script (CDG, BOD, FCO, LIS).
 */
@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportRepository airportRepository;

    @GetMapping
    public ResponseEntity<List<Airport>> getAll() {
        return ResponseEntity.ok(airportRepository.findAll());
    }

    @GetMapping("/{airportId}")
    public ResponseEntity<Airport> getById(@PathVariable String airportId) {
        return airportRepository.findById(airportId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
