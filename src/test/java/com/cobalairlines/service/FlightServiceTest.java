package com.cobalairlines.service;

import com.cobalairlines.dto.FlightDTO;
import com.cobalairlines.dto.FlightSearchRequest;
import com.cobalairlines.entity.Airport;
import com.cobalairlines.entity.Flight;
import com.cobalairlines.exception.ResourceNotFoundException;
import com.cobalairlines.repository.AirplaneRepository;
import com.cobalairlines.repository.AirportRepository;
import com.cobalairlines.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AirplaneRepository airplaneRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight flight;
    private Airport depAirport;
    private Airport arrAirport;

    @BeforeEach
    void setUp() {
        depAirport = new Airport();
        depAirport.setAirportId("LIS");
        depAirport.setCity("Lisbon");

        arrAirport = new Airport();
        arrAirport.setAirportId("OPO");
        arrAirport.setCity("Porto");

        flight = new Flight();
        flight.setFlightId(1);
        flight.setFlightNum("CB001");
        flight.setFlightDate(LocalDate.of(2026, 4, 1));
        flight.setDepTime(LocalTime.of(8, 0));
        flight.setArrTime(LocalTime.of(9, 0));
        flight.setTotalPassengers(0);
        flight.setTotalBaggage(0);
        flight.setDepartureAirport(depAirport);
        flight.setArrivalAirport(arrAirport);
    }

    @Test
    void getById_existingFlight_returnsFlightDTO() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(flight));

        FlightDTO dto = flightService.getById(1);

        assertThat(dto.getFlightNum()).isEqualTo("CB001");
        assertThat(dto.getDepartureAirportId()).isEqualTo("LIS");
        assertThat(dto.getArrivalAirportId()).isEqualTo("OPO");
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(flightRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.getById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAllFlights_returnsMappedList() {
        when(flightRepository.findAll()).thenReturn(List.of(flight));

        List<FlightDTO> result = flightService.getAllFlights();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFlightNum()).isEqualTo("CB001");
    }

    @Test
    void searchFlights_delegatesToRepository() {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setFlightNum("CB001");
        req.setFlightDate(LocalDate.of(2026, 4, 1));

        when(flightRepository.searchFlights("CB001", null, null, LocalDate.of(2026, 4, 1)))
                .thenReturn(List.of(flight));

        List<FlightDTO> result = flightService.searchFlights(req);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFlightNum()).isEqualTo("CB001");
    }

    @Test
    void duplicateFlightForMonth_createsFlightsForEachDay() {
        when(flightRepository.findByFlightNum("CB001")).thenReturn(List.of(flight));
        when(flightRepository.findByFlightNumAndFlightDate(any(), any())).thenReturn(Optional.empty());
        when(flightRepository.save(any(Flight.class))).thenAnswer(inv -> {
            Flight f = inv.getArgument(0);
            f.setFlightId(99);
            return f;
        });

        List<FlightDTO> result = flightService.duplicateFlightForMonth("CB001", 2026, 4);

        assertThat(result).hasSize(30); // April has 30 days
        verify(flightRepository, times(30)).save(any(Flight.class));
    }

    @Test
    void duplicateFlightForMonth_skipsExistingDates() {
        when(flightRepository.findByFlightNum("CB001")).thenReturn(List.of(flight));
        when(flightRepository.findByFlightNumAndFlightDate(eq("CB001"), any()))
                .thenReturn(Optional.of(flight)); // all dates already exist

        List<FlightDTO> result = flightService.duplicateFlightForMonth("CB001", 2026, 4);

        assertThat(result).isEmpty();
        verify(flightRepository, never()).save(any());
    }

    @Test
    void duplicateFlightForMonth_flightNotFound_throwsException() {
        when(flightRepository.findByFlightNum("UNKNOWN")).thenReturn(List.of());

        assertThatThrownBy(() -> flightService.duplicateFlightForMonth("UNKNOWN", 2026, 4))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteFlight_callsRepositoryDeleteById() {
        flightService.deleteFlight(1);
        verify(flightRepository).deleteById(1);
    }
}
