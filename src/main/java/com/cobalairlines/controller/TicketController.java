package com.cobalairlines.controller;

import com.cobalairlines.dto.TicketDTO;
import com.cobalairlines.dto.TicketSaleRequest;
import com.cobalairlines.dto.TicketSearchRequest;
import com.cobalairlines.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Ticket controller — replaces SELL1-COB, SRCHTKT-COB, PRINT-TICKET-COB, RECEIPT-COB.
 *
 * Endpoints:
 *   POST /api/tickets/search          -> multi-criteria search (SRCHTKT-COB)
 *   GET  /api/tickets/{id}            -> single ticket
 *   POST /api/tickets/sell            -> sell tickets (SELL1-COB + SELL2)
 *   GET  /api/tickets/{id}/boarding-pass  -> text boarding pass (PRINT-TICKET-COB)
 *   GET  /api/tickets/receipt/{buyId} -> text receipt (RECEIPT-COB)
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /** Ticket search — replaces SRCHTKT-COB. PF5 on SELL1 map. */
    @PostMapping("/search")
    public ResponseEntity<List<TicketDTO>> searchTickets(@RequestBody TicketSearchRequest req) {
        return ResponseEntity.ok(ticketService.searchTickets(req));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketDTO> getById(@PathVariable String ticketId) {
        return ResponseEntity.ok(ticketService.getById(ticketId));
    }

    /**
     * Sell tickets — replaces SELL1-COB validation + SELL2 transaction.
     * Price hardcoded at 120.99/ticket (same as original COBOL).
     * Requires SALES role (DEPTID=7).
     */
    @PostMapping("/sell")
    @PreAuthorize("hasRole('SALES')")
    public ResponseEntity<List<TicketDTO>> sellTickets(@Valid @RequestBody TicketSaleRequest req) {
        return ResponseEntity.ok(ticketService.sellTickets(req));
    }

    /**
     * Boarding pass — replaces PRINT-TICKET-COB batch program.
     * Returns plain text matching original batch output format.
     */
    @GetMapping(value = "/{ticketId}/boarding-pass", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getBoardingPass(@PathVariable String ticketId) {
        return ResponseEntity.ok(ticketService.generateBoardingPass(ticketId));
    }

    /**
     * Receipt — replaces RECEIPT-COB batch program.
     * Returns plain text matching original batch output format.
     */
    @GetMapping(value = "/receipt/{buyId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getReceipt(@PathVariable Integer buyId) {
        return ResponseEntity.ok(ticketService.generateReceipt(buyId));
    }
}
