package com.cobalairlines.service;

import com.cobalairlines.dto.TicketDTO;
import com.cobalairlines.dto.TicketSaleRequest;
import com.cobalairlines.dto.TicketSearchRequest;
import com.cobalairlines.entity.*;
import com.cobalairlines.exception.ResourceNotFoundException;
import com.cobalairlines.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ticket service — migrated from:
 *   - SELL1-COB  (CICS ticket sale, stage 1 — validation + price calculation)
 *   - SRCHTKT-COB (CICS ticket search)
 *   - PRINT-TICKET-COB (batch boarding pass generation)
 *   - RECEIPT-COB (batch receipt generation)
 *
 * Original price (SELL1-COB hardcoded):
 *   MOVE 120.99 TO WS-PRICE
 *
 * Original SELL1-COB validations (enforced here via @Valid + service checks):
 *   IF WS-CLIENTID NOT NUMERIC OR WS-CLIENTID = 0  -> error
 *   IF WS-FLIGHTNUM = SPACES                        -> error
 *   IF WS-DATE NOT YYYY-MM-DD                       -> error
 *   IF WS-PASSCOUNT NOT NUMERIC OR WS-PASSCOUNT = 0 -> error
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    /** Hardcoded price from SELL1-COB: MOVE 120.99 TO WS-PRICE */
    public static final BigDecimal PRICE_PER_TICKET = new BigDecimal("120.99");

    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final EmployeeRepository employeeRepository;
    private final BuyRepository buyRepository;

    /** Ticket search — replaces SRCHTKT-COB multi-criteria SQL query */
    public List<TicketDTO> searchTickets(TicketSearchRequest req) {
        return ticketRepository.searchTickets(
                req.getTicketId(),
                req.getClientId(),
                req.getFirstName(),
                req.getLastName(),
                req.getFlightId(),
                req.getFlightDate()
        ).stream().map(TicketDTO::from).collect(Collectors.toList());
    }

    public TicketDTO getById(String ticketId) {
        return TicketDTO.from(ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId)));
    }

    /**
     * Sell tickets — replaces SELL1-COB + SELL2 (complete transaction).
     *
     * SELL1-COB flow:
     *   1. Validate inputs
     *   2. SELECT client from PASSENGERS
     *   3. SELECT flight from FLIGHT by flightnum + date
     *   4. Calculate price = passcount * 120.99
     *   5. XCTL to SELL2 (passing COMMAREA) — here: we complete the sale directly
     *
     * SELL2 (not implemented in COBOL) inferred behavior:
     *   6. INSERT INTO BUY (buydate, buytime, price, empid, clientid)
     *   7. INSERT INTO TICKET (ticketid, buyid, clientid, flightid, seatnum) x passcount
     */
    @Transactional
    public List<TicketDTO> sellTickets(TicketSaleRequest req) {
        // Step 2: validate client
        Passenger passenger = passengerRepository.findById(req.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found: " + req.getClientId()));

        // Step 3: validate flight
        Flight flight = flightRepository.findByFlightNumAndFlightDate(req.getFlightNum(), req.getFlightDate())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Flight not found: " + req.getFlightNum() + " on " + req.getFlightDate()));

        Employee salesEmp = employeeRepository.findByEmpId(req.getSalesEmpId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sales employee not found: " + req.getSalesEmpId()));

        // Step 4: price = passengerCount * 120.99
        BigDecimal totalPrice = PRICE_PER_TICKET.multiply(BigDecimal.valueOf(req.getPassengerCount()));

        // Step 6: INSERT INTO BUY
        Buy buy = new Buy();
        buy.setBuyDate(LocalDate.now());
        buy.setBuyTime(LocalTime.now());
        buy.setPrice(totalPrice);
        buy.setEmployee(salesEmp);
        buy.setPassenger(passenger);
        buy = buyRepository.save(buy);

        // Step 7: INSERT INTO TICKET (one per passenger)
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= req.getPassengerCount(); i++) {
            Ticket ticket = new Ticket();
            ticket.setTicketId(generateTicketId(buy.getBuyId(), i));
            ticket.setBuy(buy);
            ticket.setPassenger(passenger);
            ticket.setFlight(flight);
            ticket.setSeatNum(assignSeat(flight, i));
            tickets.add(ticketRepository.save(ticket));
        }

        // Update flight passenger count
        flight.setTotalPassengers(
                (flight.getTotalPassengers() == null ? 0 : flight.getTotalPassengers())
                + req.getPassengerCount()
        );
        flightRepository.save(flight);

        log.info("Sold {} ticket(s) for flight {} on {} — buy#{} total={}",
                req.getPassengerCount(), req.getFlightNum(), req.getFlightDate(),
                buy.getBuyId(), totalPrice);

        return tickets.stream().map(TicketDTO::from).collect(Collectors.toList());
    }

    /**
     * Generate boarding pass text — migrated from PRINT-TICKET-COB.
     *
     * Original output format (PRINT-TICKET-COB):
     *   *** COBAL AIRLINES BOARDING PASS ***
     *   PASSENGER: [NAME]        SEAT: [SEATNUM]
     *   FLIGHT: [FLIGHTNUM]      DATE: [DATE]
     *   FROM: [DEP AIRPORT]      TO: [ARR AIRPORT]
     *   DEPARTURE: [DEPTIME]
     */
    public String generateBoardingPass(String ticketId) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));

        Flight f = t.getFlight();
        Passenger p = t.getPassenger();

        return String.format("""
                *** COBAL AIRLINES BOARDING PASS ***
                =====================================
                PASSENGER : %s %s
                SEAT      : %s
                FLIGHT    : %s
                DATE      : %s
                FROM      : %s (%s)
                TO        : %s (%s)
                DEPARTURE : %s
                ARRIVAL   : %s
                =====================================
                TICKET ID : %s
                """,
                p.getFirstName(), p.getLastName(),
                t.getSeatNum(),
                f.getFlightNum(),
                f.getFlightDate(),
                f.getDepartureAirport().getAirportId(), f.getDepartureAirport().getCity(),
                f.getArrivalAirport().getAirportId(), f.getArrivalAirport().getCity(),
                f.getDepTime(),
                f.getArrTime(),
                t.getTicketId()
        );
    }

    /**
     * Generate receipt — migrated from RECEIPT-COB.
     *
     * Original output (RECEIPT-COB):
     *   *** COBAL AIRLINES RECEIPT ***
     *   BUY ID: [BUYID]   DATE: [DATE]  TIME: [TIME]
     *   TOTAL: [PRICE]
     *   PLEASE KEEP YOUR TICKETS
     */
    public String generateReceipt(Integer buyId) {
        Buy buy = buyRepository.findById(buyId)
                .orElseThrow(() -> new ResourceNotFoundException("Buy record not found: " + buyId));

        List<Ticket> tickets = ticketRepository.findByPassengerClientId(buy.getPassenger().getClientId())
                .stream().filter(t -> t.getBuy().getBuyId().equals(buyId)).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("*** COBAL AIRLINES RECEIPT ***\n");
        sb.append("==============================\n");
        sb.append(String.format("BUY ID  : %d%n", buy.getBuyId()));
        sb.append(String.format("DATE    : %s%n", buy.getBuyDate()));
        sb.append(String.format("TIME    : %s%n", buy.getBuyTime()));
        sb.append(String.format("TOTAL   : %.2f EUR%n", buy.getPrice()));
        sb.append("------------------------------\n");
        sb.append("TICKETS:\n");
        for (Ticket t : tickets) {
            sb.append(String.format("  %s  SEAT %s  FLIGHT %s  %s%n",
                    t.getTicketId(), t.getSeatNum(),
                    t.getFlight().getFlightNum(), t.getFlight().getFlightDate()));
        }
        sb.append("==============================\n");
        sb.append("PLEASE KEEP YOUR TICKETS\n");

        return sb.toString();
    }

    private String generateTicketId(Integer buyId, int seq) {
        return String.format("T%07d%02d", buyId, seq);
    }

    private String assignSeat(Flight flight, int seq) {
        // Simple sequential seat assignment (A01..Z99)
        char row = (char) ('A' + ((seq - 1) / 9));
        int col = ((seq - 1) % 9) + 1;
        return String.format("%c%02d", row, col);
    }
}
