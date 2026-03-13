package com.cobalairlines.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Migrated from DB2 TICKET table and TICKET-DCLGEN copybook.
 *
 * Original COBOL DB2 definition:
 *   TICKETID  CHAR(10)   PRIMARY KEY
 *   BUYID     INT        FK -> BUY
 *   CLIENTID  INT        FK -> PASSENGERS
 *   FLIGHTID  INT        FK -> FLIGHT
 *   SEATNUM   CHAR(3)
 *
 * Boarding pass: migrated from PRINT-TICKET-COB (batch program).
 * Receipt:       migrated from RECEIPT-COB (batch program).
 */
@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @Column(name = "ticket_id", length = 10)
    @NotBlank
    private String ticketId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buy_id", nullable = false)
    private Buy buy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "seat_num", length = 3)
    private String seatNum;
}
