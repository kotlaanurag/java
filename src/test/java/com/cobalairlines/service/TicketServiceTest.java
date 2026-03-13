package com.cobalairlines.service;

import com.cobalairlines.dto.TicketDTO;
import com.cobalairlines.dto.TicketSaleRequest;
import com.cobalairlines.entity.*;
import com.cobalairlines.exception.ResourceNotFoundException;
import com.cobalairlines.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private FlightRepository flightRepository;
    @Mock private PassengerRepository passengerRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private BuyRepository buyRepository;

    @InjectMocks
    private TicketService ticketService;

    private Passenger passenger;
    private Flight flight;
    private Employee salesEmp;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department(7, "Sales", null);

        passenger = new Passenger();
        passenger.setClientId(1);
        passenger.setFirstName("Alice");
        passenger.setLastName("Smith");

        Airport dep = new Airport();
        dep.setAirportId("LIS");
        dep.setCity("Lisbon");

        Airport arr = new Airport();
        arr.setAirportId("OPO");
        arr.setCity("Porto");

        flight = new Flight();
        flight.setFlightId(10);
        flight.setFlightNum("CB001");
        flight.setFlightDate(LocalDate.of(2026, 4, 1));
        flight.setDepTime(LocalTime.of(8, 0));
        flight.setArrTime(LocalTime.of(9, 0));
        flight.setTotalPassengers(0);
        flight.setDepartureAirport(dep);
        flight.setArrivalAirport(arr);

        salesEmp = new Employee();
        salesEmp.setEmpId("EMP007");
        salesEmp.setFirstName("Bob");
        salesEmp.setLastName("Jones");
        salesEmp.setDepartment(department);
    }

    @Test
    void sellTickets_createsCorrectNumberOfTickets() {
        TicketSaleRequest req = new TicketSaleRequest();
        req.setClientId(1);
        req.setFlightNum("CB001");
        req.setFlightDate(LocalDate.of(2026, 4, 1));
        req.setSalesEmpId("EMP007");
        req.setPassengerCount(2);

        Buy savedBuy = new Buy();
        savedBuy.setBuyId(100);
        savedBuy.setPrice(new BigDecimal("241.98"));
        savedBuy.setBuyDate(LocalDate.now());
        savedBuy.setPassenger(passenger);

        when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));
        when(flightRepository.findByFlightNumAndFlightDate("CB001", LocalDate.of(2026, 4, 1)))
                .thenReturn(Optional.of(flight));
        when(employeeRepository.findByEmpId("EMP007")).thenReturn(Optional.of(salesEmp));
        when(buyRepository.save(any(Buy.class))).thenReturn(savedBuy);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setBuy(savedBuy);
            t.setPassenger(passenger);
            t.setFlight(flight);
            return t;
        });
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        List<TicketDTO> result = ticketService.sellTickets(req);

        assertThat(result).hasSize(2);
        verify(ticketRepository, times(2)).save(any(Ticket.class));
    }

    @Test
    void sellTickets_totalPriceCalculatedCorrectly() {
        TicketSaleRequest req = new TicketSaleRequest();
        req.setClientId(1);
        req.setFlightNum("CB001");
        req.setFlightDate(LocalDate.of(2026, 4, 1));
        req.setSalesEmpId("EMP007");
        req.setPassengerCount(3);

        Buy savedBuy = new Buy();
        savedBuy.setBuyId(101);
        savedBuy.setPassenger(passenger);

        when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));
        when(flightRepository.findByFlightNumAndFlightDate(any(), any())).thenReturn(Optional.of(flight));
        when(employeeRepository.findByEmpId("EMP007")).thenReturn(Optional.of(salesEmp));
        when(buyRepository.save(any(Buy.class))).thenAnswer(inv -> {
            Buy b = inv.getArgument(0);
            // Verify total price = 3 * 120.99 = 362.97
            assertThat(b.getPrice()).isEqualByComparingTo(new BigDecimal("362.97"));
            b.setBuyId(101);
            b.setPassenger(passenger);
            return b;
        });
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        ticketService.sellTickets(req);
    }

    @Test
    void sellTickets_passengerNotFound_throwsException() {
        TicketSaleRequest req = new TicketSaleRequest();
        req.setClientId(999);
        req.setFlightNum("CB001");
        req.setFlightDate(LocalDate.of(2026, 4, 1));
        req.setSalesEmpId("EMP007");
        req.setPassengerCount(1);

        when(passengerRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.sellTickets(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void sellTickets_flightNotFound_throwsException() {
        TicketSaleRequest req = new TicketSaleRequest();
        req.setClientId(1);
        req.setFlightNum("UNKNOWN");
        req.setFlightDate(LocalDate.of(2026, 4, 1));
        req.setSalesEmpId("EMP007");
        req.setPassengerCount(1);

        when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));
        when(flightRepository.findByFlightNumAndFlightDate("UNKNOWN", LocalDate.of(2026, 4, 1)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.sellTickets(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById_notFound_throwsException() {
        when(ticketRepository.findById("T00000001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.getById("T00000001"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void generateBoardingPass_containsPassengerAndFlightInfo() {
        Ticket ticket = new Ticket();
        ticket.setTicketId("T000000101");
        ticket.setSeatNum("A01");
        ticket.setPassenger(passenger);
        ticket.setFlight(flight);
        Buy buy = new Buy();
        buy.setBuyId(1);
        ticket.setBuy(buy);

        when(ticketRepository.findById("T000000101")).thenReturn(Optional.of(ticket));

        String boardingPass = ticketService.generateBoardingPass("T000000101");

        assertThat(boardingPass)
                .contains("Alice", "Smith")
                .contains("A01")
                .contains("CB001")
                .contains("LIS")
                .contains("OPO")
                .contains("COBAL AIRLINES BOARDING PASS");
    }

    @Test
    void generateReceipt_containsBuyAndTicketInfo() {
        Buy buy = new Buy();
        buy.setBuyId(100);
        buy.setBuyDate(LocalDate.of(2026, 4, 1));
        buy.setBuyTime(LocalTime.of(10, 30));
        buy.setPrice(new BigDecimal("241.98"));
        buy.setPassenger(passenger);

        Ticket ticket = new Ticket();
        ticket.setTicketId("T000010001");
        ticket.setSeatNum("A01");
        ticket.setPassenger(passenger);
        ticket.setFlight(flight);
        ticket.setBuy(buy);

        when(buyRepository.findById(100)).thenReturn(Optional.of(buy));
        when(ticketRepository.findByPassengerClientId(1)).thenReturn(List.of(ticket));

        String receipt = ticketService.generateReceipt(100);

        assertThat(receipt)
                .contains("100")
                .contains("241.98")
                .contains("COBAL AIRLINES RECEIPT")
                .contains("T000010001");
    }

    @Test
    void pricePerTicket_isCorrectConstant() {
        assertThat(TicketService.PRICE_PER_TICKET.toPlainString()).isEqualTo("120.99");
    }
}
