package com.cobalairlines.repository;

import com.cobalairlines.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Replaces DB2 queries in SRCHFLY-COB and FLIGHT-DUPLICATE-COB.
 *
 * Original COBOL (SRCHFLY-COB):
 *   EXEC SQL SELECT FLIGHTID, DEPTIME, ARRTIME, AIRPORTDEP, AIRPORTARR
 *            FROM FLIGHT
 *            WHERE FLIGHTNUM = :WS-FLIGHTNUM
 *              AND FLIGHTDATE = :WS-DATE END-EXEC
 */
@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    List<Flight> findByFlightNum(String flightNum);

    List<Flight> findByFlightDate(LocalDate flightDate);

    Optional<Flight> findByFlightNumAndFlightDate(String flightNum, LocalDate flightDate);

    List<Flight> findByDepartureAirportAirportId(String airportId);

    List<Flight> findByArrivalAirportAirportId(String airportId);

    /**
     * Multi-criteria search — migrated from SRCHFLY-COB which supported
     * search by flightnum, departure airport, arrival airport, and date.
     */
    @Query("""
        SELECT f FROM Flight f
        WHERE (:flightNum IS NULL OR f.flightNum = :flightNum)
          AND (:depAirport IS NULL OR f.departureAirport.airportId = :depAirport)
          AND (:arrAirport IS NULL OR f.arrivalAirport.airportId = :arrAirport)
          AND (:flightDate IS NULL OR f.flightDate = :flightDate)
        ORDER BY f.flightDate, f.depTime
        """)
    List<Flight> searchFlights(
            @Param("flightNum")  String flightNum,
            @Param("depAirport") String depAirport,
            @Param("arrAirport") String arrAirport,
            @Param("flightDate") LocalDate flightDate
    );
}
