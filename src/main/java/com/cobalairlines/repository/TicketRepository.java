package com.cobalairlines.repository;

import com.cobalairlines.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Replaces DB2 queries in SRCHTKT-COB.
 *
 * Original COBOL supported search by:
 *   ticket ID, client ID, first/last name, flight ID, flight date.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {

    List<Ticket> findByPassengerClientId(Integer clientId);

    List<Ticket> findByFlightFlightId(Integer flightId);

    /**
     * Multi-criteria ticket search — migrated from SRCHTKT-COB.
     */
    @Query("""
        SELECT t FROM Ticket t
        WHERE (:ticketId  IS NULL OR t.ticketId = :ticketId)
          AND (:clientId  IS NULL OR t.passenger.clientId = :clientId)
          AND (:firstName IS NULL OR LOWER(t.passenger.firstName) = LOWER(:firstName))
          AND (:lastName  IS NULL OR LOWER(t.passenger.lastName)  = LOWER(:lastName))
          AND (:flightId  IS NULL OR t.flight.flightId = :flightId)
          AND (:flightDate IS NULL OR t.flight.flightDate = :flightDate)
        ORDER BY t.ticketId
        """)
    List<Ticket> searchTickets(
            @Param("ticketId")   String ticketId,
            @Param("clientId")   Integer clientId,
            @Param("firstName")  String firstName,
            @Param("lastName")   String lastName,
            @Param("flightId")   Integer flightId,
            @Param("flightDate") LocalDate flightDate
    );
}
