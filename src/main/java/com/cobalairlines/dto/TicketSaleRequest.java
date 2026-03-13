package com.cobalairlines.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

/**
 * Migrated from SELL1 BMS map input fields (SELL1-COB).
 *
 * Original COBOL validations (preserved as Bean Validation):
 *   - CLIENTID must be numeric and non-zero
 *   - FLIGHTNUM must not be blank
 *   - DATE format must be YYYY-MM-DD
 *   - PASSENGERCOUNT must be numeric and non-zero
 *
 * Original price: 120.99 per ticket (hardcoded in SELL1-COB).
 * This service uses the same constant: TicketService.PRICE_PER_TICKET.
 */
@Data
public class TicketSaleRequest {

    @NotNull(message = "Client ID is required")
    @Positive(message = "Client ID must be positive")
    private Integer clientId;

    @NotBlank(message = "Flight number is required")
    private String flightNum;

    @NotNull(message = "Flight date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate flightDate;

    @NotNull(message = "Passenger count is required")
    @Positive(message = "Passenger count must be at least 1")
    private Integer passengerCount;

    /** Employee ID of the sales agent processing this transaction */
    @NotBlank(message = "Sales employee ID is required")
    private String salesEmpId;
}
