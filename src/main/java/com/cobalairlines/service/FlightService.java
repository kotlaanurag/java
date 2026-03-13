package com.cobalairlines.service;

import com.cobalairlines.dto.FlightDTO;
import com.cobalairlines.dto.FlightSearchRequest;
import com.cobalairlines.entity.Flight;
import com.cobalairlines.exception.ResourceNotFoundException;
import com.cobalairlines.repository.AirplaneRepository;
import com.cobalairlines.repository.AirportRepository;
import com.cobalairlines.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Flight service — migrated from:
 *   - SRCHFLY-COB (CICS flight search)
 *   - FLIGHT-DUPLICATE-COB / CBFLIGHT (batch flight duplication)
 *
 * Original SRCHFLY-COB SQL:
 *   EXEC SQL SELECT FLIGHTID, DEPTIME, ARRTIME, AIRPORTDEP, AIRPORTARR
 *            FROM FLIGHT
 *            WHERE FLIGHTNUM  = :WS-FLIGHTNUM
 *              AND FLIGHTDATE = :WS-DATE END-EXEC
 *
 * Original CBFLIGHT:
 *   Processed 8 flight numbers, duplicated each for all 31 days of month.
 *   EXEC SQL INSERT INTO FLIGHT VALUES (:WS-FLIGHT-FIELDS) END-EXEC
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AirplaneRepository airplaneRepository;

    /** Search flights — replaces SRCHFLY-COB multi-criteria query */
    public List<FlightDTO> searchFlights(FlightSearchRequest req) {
        return flightRepository.searchFlights(
                req.getFlightNum(),
                req.getDepartureAirportId(),
                req.getArrivalAirportId(),
                req.getFlightDate()
        ).stream().map(FlightDTO::from).collect(Collectors.toList());
    }

    public FlightDTO getById(Integer flightId) {
        return FlightDTO.from(flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightId)));
    }

    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll().stream().map(FlightDTO::from).collect(Collectors.toList());
    }

    /**
     * Duplicate a flight for every day in a given month/year.
     *
     * Migrated from FLIGHT-DUPLICATE-COB (CBFLIGHT):
     *   Original: iterated 31 days, called INSERT for each day.
     *   WS-MONTH-TABLE: 31 entries, each day 01..31.
     *   Processed 8 hardcoded flight numbers in sequence.
     *
     * @param flightNum  source flight to duplicate
     * @param year       target year
     * @param month      target month (1-12)
     * @return list of created flights
     */
    @Transactional
    public List<FlightDTO> duplicateFlightForMonth(String flightNum, int year, int month) {
        Flight source = flightRepository.findByFlightNum(flightNum).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightNum));

        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        List<Flight> created = new ArrayList<>();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            // Skip if already exists for that date
            if (flightRepository.findByFlightNumAndFlightDate(flightNum, date).isPresent()) {
                continue;
            }
            Flight copy = new Flight();
            copy.setFlightNum(source.getFlightNum());
            copy.setFlightDate(date);
            copy.setDepTime(source.getDepTime());
            copy.setArrTime(source.getArrTime());
            copy.setTotalPassengers(0);
            copy.setTotalBaggage(0);
            copy.setAirplane(source.getAirplane());
            copy.setDepartureAirport(source.getDepartureAirport());
            copy.setArrivalAirport(source.getArrivalAirport());
            copy.setShift(source.getShift());
            created.add(flightRepository.save(copy));
        }

        log.info("Duplicated flight {} for {}/{}: {} new records created",
                flightNum, year, month, created.size());

        return created.stream().map(FlightDTO::from).collect(Collectors.toList());
    }

    @Transactional
    public FlightDTO createFlight(Flight flight) {
        return FlightDTO.from(flightRepository.save(flight));
    }

    @Transactional
    public void deleteFlight(Integer flightId) {
        flightRepository.deleteById(flightId);
    }
}
