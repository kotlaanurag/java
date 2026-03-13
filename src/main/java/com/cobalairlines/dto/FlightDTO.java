package com.cobalairlines.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.cobalairlines.entity.Flight;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Flight response DTO — replaces SRCHFLY-COB screen output fields.
 *
 * Original CICS output to map:
 *   MOVE WS-DEPTIME  TO SRCHFLIOMP(DEPTIMEO)
 *   MOVE WS-ARRTIME  TO SRCHFLIOMP(ARRTIMEO)
 *   MOVE WS-AIRPDEP  TO SRCHFLIOMP(AIRPDEP)
 *   MOVE WS-AIPARR   TO SRCHFLIOMP(AIPARR)
 *   MOVE WS-FLIGHTID TO SRCHFLIOMP(FLIGHTIDO)
 */
@Data
public class FlightDTO {
    private Integer flightId;
    private String flightNum;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate flightDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime depTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime arrTime;

    private String departureAirportId;
    private String departureCity;
    private String arrivalAirportId;
    private String arrivalCity;
    private String airplaneId;
    private String airplaneType;
    private Integer totalPassengers;
    private Integer totalBaggage;

    public static FlightDTO from(Flight f) {
        FlightDTO dto = new FlightDTO();
        dto.setFlightId(f.getFlightId());
        dto.setFlightNum(f.getFlightNum());
        dto.setFlightDate(f.getFlightDate());
        dto.setDepTime(f.getDepTime());
        dto.setArrTime(f.getArrTime());
        dto.setTotalPassengers(f.getTotalPassengers());
        dto.setTotalBaggage(f.getTotalBaggage());
        if (f.getDepartureAirport() != null) {
            dto.setDepartureAirportId(f.getDepartureAirport().getAirportId());
            dto.setDepartureCity(f.getDepartureAirport().getCity());
        }
        if (f.getArrivalAirport() != null) {
            dto.setArrivalAirportId(f.getArrivalAirport().getAirportId());
            dto.setArrivalCity(f.getArrivalAirport().getCity());
        }
        if (f.getAirplane() != null) {
            dto.setAirplaneId(f.getAirplane().getAirplaneId());
            dto.setAirplaneType(f.getAirplane().getType());
        }
        return dto;
    }
}
