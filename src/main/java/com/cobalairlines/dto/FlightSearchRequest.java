package com.cobalairlines.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * Migrated from SRCHFLI BMS map input fields.
 *
 * Original CICS (SRCHFLY-COB) search parameters:
 *   - Flight number  (FLIGHTNUM CHAR 6)
 *   - Departure airport (AIRPORTDEP CHAR 4)
 *   - Arrival airport   (AIRPORTARR CHAR 4)
 *   - Flight date       (FLIGHTDATE DATE)
 *
 * All fields optional — any combination triggers a filtered SQL query.
 */
@Data
public class FlightSearchRequest {
    private String flightNum;
    private String departureAirportId;
    private String arrivalAirportId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate flightDate;
}
