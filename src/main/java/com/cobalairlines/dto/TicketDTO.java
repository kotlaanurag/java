package com.cobalairlines.dto;

import com.cobalairlines.entity.Ticket;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Ticket response — replaces SRCHTKT-COB screen output and
 * PRINT-TICKET-COB boarding pass fields.
 *
 * Original boarding pass fields (PRINT-TICKET-COB):
 *   Passenger name, seat number, flight number,
 *   airports, date, departure time
 */
@Data
public class TicketDTO {
    private String ticketId;
    private String seatNum;

    // Passenger info
    private Integer clientId;
    private String passengerFirstName;
    private String passengerLastName;

    // Flight info
    private Integer flightId;
    private String flightNum;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate flightDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime depTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime arrTime;

    private String departureAirport;
    private String arrivalAirport;

    // Buy info (receipt)
    private Integer buyId;
    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate buyDate;

    public static TicketDTO from(Ticket t) {
        TicketDTO dto = new TicketDTO();
        dto.setTicketId(t.getTicketId());
        dto.setSeatNum(t.getSeatNum());

        if (t.getPassenger() != null) {
            dto.setClientId(t.getPassenger().getClientId());
            dto.setPassengerFirstName(t.getPassenger().getFirstName());
            dto.setPassengerLastName(t.getPassenger().getLastName());
        }
        if (t.getFlight() != null) {
            dto.setFlightId(t.getFlight().getFlightId());
            dto.setFlightNum(t.getFlight().getFlightNum());
            dto.setFlightDate(t.getFlight().getFlightDate());
            dto.setDepTime(t.getFlight().getDepTime());
            dto.setArrTime(t.getFlight().getArrTime());
            if (t.getFlight().getDepartureAirport() != null)
                dto.setDepartureAirport(t.getFlight().getDepartureAirport().getAirportId());
            if (t.getFlight().getArrivalAirport() != null)
                dto.setArrivalAirport(t.getFlight().getArrivalAirport().getAirportId());
        }
        if (t.getBuy() != null) {
            dto.setBuyId(t.getBuy().getBuyId());
            dto.setPrice(t.getBuy().getPrice());
            dto.setBuyDate(t.getBuy().getBuyDate());
        }
        return dto;
    }
}
