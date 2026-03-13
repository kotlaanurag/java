package com.cobalairlines.controller;

import com.cobalairlines.dto.PassengerDTO;
import com.cobalairlines.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Passenger controller — replaces PASSENGER-INSERT-MAINPROG batch job.
 *
 * Endpoints:
 *   GET  /api/passengers            -> list all
 *   GET  /api/passengers/{id}       -> get by client ID
 *   GET  /api/passengers/search     -> search by name
 *   POST /api/passengers            -> create single passenger
 *   POST /api/passengers/import     -> bulk import (replaces PASSENG batch + 8 XML files)
 *   DELETE /api/passengers/{id}     -> delete
 */
@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public ResponseEntity<List<PassengerDTO>> getAll() {
        return ResponseEntity.ok(passengerService.getAll());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<PassengerDTO> getById(@PathVariable Integer clientId) {
        return ResponseEntity.ok(passengerService.getById(clientId));
    }

    /**
     * Name-based search — replaces SRCHTKT-COB passenger lookup.
     * GET /api/passengers/search?firstName=John&lastName=Doe
     */
    @GetMapping("/search")
    public ResponseEntity<List<PassengerDTO>> search(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        return ResponseEntity.ok(passengerService.search(firstName, lastName));
    }

    @PostMapping
    public ResponseEntity<PassengerDTO> create(@Valid @RequestBody PassengerDTO dto) {
        return ResponseEntity.ok(passengerService.create(dto));
    }

    /**
     * Bulk import — replaces PASSENG + SUXML batch programs.
     * Original: read 8 UTF-8 XML files, converted EBCDIC, inserted up to 80 passengers.
     * Here: accepts a JSON array of up to 80 passengers.
     */
    @PostMapping("/import")
    public ResponseEntity<List<PassengerDTO>> bulkImport(@RequestBody List<@Valid PassengerDTO> passengers) {
        return ResponseEntity.ok(passengerService.bulkImport(passengers));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(@PathVariable Integer clientId) {
        passengerService.delete(clientId);
        return ResponseEntity.noContent().build();
    }
}
