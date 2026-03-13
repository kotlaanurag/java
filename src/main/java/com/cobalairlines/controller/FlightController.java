package com.cobalairlines.controller;

import com.cobalairlines.dto.FlightDTO;
import com.cobalairlines.dto.FlightSearchRequest;
import com.cobalairlines.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Flight controller — replaces SRCHFLY-COB (CICS) and CBFLIGHT (batch).
 *
 * Endpoints:
 *   GET  /api/flights             -> list all (replaces full table scan)
 *   GET  /api/flights/{id}        -> get by ID
 *   POST /api/flights/search      -> multi-criteria search (replaces SRCHFLY-COB)
 *   POST /api/flights/duplicate   -> month duplication (replaces CBFLIGHT batch)
 *   POST /api/flights             -> create new flight
 *   DELETE /api/flights/{id}      -> delete
 */
@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDTO> getById(@PathVariable Integer flightId) {
        return ResponseEntity.ok(flightService.getById(flightId));
    }

    /**
     * Multi-criteria flight search — migrated from SRCHFLY-COB.
     * Original: PF4 key on SELL1 map opened flight search screen.
     * Params: flightNum, departureAirportId, arrivalAirportId, flightDate
     */
    @PostMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(@RequestBody FlightSearchRequest req) {
        return ResponseEntity.ok(flightService.searchFlights(req));
    }

    /**
     * Duplicate flight for entire month — migrated from CBFLIGHT batch program.
     * Original: hardcoded 8 flight numbers, duplicated for all 31 days.
     * Here: on-demand per flight number + year/month.
     *
     * POST /api/flights/duplicate?flightNum=AF001&year=2024&month=3
     */
    @PostMapping("/duplicate")
    @PreAuthorize("hasAnyRole('CEO', 'SCHEDULE')")
    public ResponseEntity<List<FlightDTO>> duplicateForMonth(
            @RequestParam String flightNum,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(flightService.duplicateFlightForMonth(flightNum, year, month));
    }

    @DeleteMapping("/{flightId}")
    @PreAuthorize("hasAnyRole('CEO', 'SCHEDULE')")
    public ResponseEntity<Void> deleteFlight(@PathVariable Integer flightId) {
        flightService.deleteFlight(flightId);
        return ResponseEntity.noContent().build();
    }
}
