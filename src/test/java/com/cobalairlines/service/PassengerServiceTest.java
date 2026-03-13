package com.cobalairlines.service;

import com.cobalairlines.dto.PassengerDTO;
import com.cobalairlines.entity.Passenger;
import com.cobalairlines.exception.ResourceNotFoundException;
import com.cobalairlines.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private PassengerService passengerService;

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setClientId(1);
        passenger.setFirstName("Alice");
        passenger.setLastName("Smith");
        passenger.setCity("Lisbon");
        passenger.setCountry("Portugal");
        passenger.setEmail("alice@example.com");
    }

    @Test
    void getById_found_returnsDTO() {
        when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));

        PassengerDTO dto = passengerService.getById(1);

        assertThat(dto.getFirstName()).isEqualTo("Alice");
        assertThat(dto.getLastName()).isEqualTo("Smith");
    }

    @Test
    void getById_notFound_throwsException() {
        when(passengerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerService.getById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_returnsMappedList() {
        when(passengerRepository.findAll()).thenReturn(List.of(passenger));

        List<PassengerDTO> result = passengerService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void search_byFirstAndLastName_usesExactMatch() {
        when(passengerRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("Alice", "Smith"))
                .thenReturn(List.of(passenger));

        List<PassengerDTO> result = passengerService.search("Alice", "Smith");

        assertThat(result).hasSize(1);
    }

    @Test
    void search_byLastNameOnly_usesLastNameMatch() {
        when(passengerRepository.findByLastNameIgnoreCase("Smith")).thenReturn(List.of(passenger));

        List<PassengerDTO> result = passengerService.search(null, "Smith");

        assertThat(result).hasSize(1);
        verify(passengerRepository).findByLastNameIgnoreCase("Smith");
    }

    @Test
    void search_noParams_returnsAll() {
        when(passengerRepository.findAll()).thenReturn(List.of(passenger));

        List<PassengerDTO> result = passengerService.search(null, null);

        assertThat(result).hasSize(1);
        verify(passengerRepository).findAll();
    }

    @Test
    void create_savesAndReturnsDTO() {
        PassengerDTO dto = new PassengerDTO();
        dto.setFirstName("Bob");
        dto.setLastName("Jones");
        dto.setCity("Porto");
        dto.setCountry("Portugal");

        Passenger saved = new Passenger();
        saved.setClientId(2);
        saved.setFirstName("Bob");
        saved.setLastName("Jones");

        when(passengerRepository.save(any(Passenger.class))).thenReturn(saved);

        PassengerDTO result = passengerService.create(dto);

        assertThat(result.getClientId()).isEqualTo(2);
        assertThat(result.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void bulkImport_savesAllPassengers() {
        PassengerDTO dto1 = new PassengerDTO();
        dto1.setFirstName("A"); dto1.setLastName("B");

        PassengerDTO dto2 = new PassengerDTO();
        dto2.setFirstName("C"); dto2.setLastName("D");

        when(passengerRepository.save(any(Passenger.class))).thenAnswer(inv -> inv.getArgument(0));

        List<PassengerDTO> result = passengerService.bulkImport(List.of(dto1, dto2));

        assertThat(result).hasSize(2);
        verify(passengerRepository, times(2)).save(any(Passenger.class));
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        passengerService.delete(1);
        verify(passengerRepository).deleteById(1);
    }
}
