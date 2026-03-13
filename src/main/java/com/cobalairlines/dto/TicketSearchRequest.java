package com.cobalairlines.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * Migrated from SRCHTKT BMS map input fields (SRCHTKT-COB).
 *
 * Original search parameters:
 *   TICKETID, CLIENTID, FIRSTNAME, LASTNAME, FLIGHTID, FLIGHTDATE
 */
@Data
public class TicketSearchRequest {
    private String ticketId;
    private Integer clientId;
    private String firstName;
    private String lastName;
    private Integer flightId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate flightDate;
}
