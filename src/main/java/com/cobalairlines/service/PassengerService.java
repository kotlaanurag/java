package com.cobalairlines.service;

import com.cobalairlines.dto.PassengerDTO;
import com.cobalairlines.entity.Passenger;
import com.cobalairlines.exception.ResourceNotFoundException;
import com.cobalairlines.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Passenger service — migrated from:
 *   - PASSENGER-INSERT-MAINPROG / PASSENGER-SUXML-SUBPROG (batch XML import)
 *   - SELL1-COB (passenger lookup by CLIENTID)
 *
 * Original batch program:
 *   Read 8 XML files (UTF-8 -> EBCDIC via NATIONAL-OF)
 *   Parse with COBOL XML PARSE statement
 *   INSERT INTO PASSENGERS (FIRSTNAME, LASTNAME, ADDRESS, CITY,
 *                           COUNTRY, ZIPCODE, TELEPHONE, EMAIL)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public PassengerDTO getById(Integer clientId) {
        return PassengerDTO.from(passengerRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found: " + clientId)));
    }

    public List<PassengerDTO> getAll() {
        return passengerRepository.findAll().stream().map(PassengerDTO::from).collect(Collectors.toList());
    }

    public List<PassengerDTO> search(String firstName, String lastName) {
        List<Passenger> results;
        if (firstName != null && lastName != null) {
            results = passengerRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
        } else if (lastName != null) {
            results = passengerRepository.findByLastNameIgnoreCase(lastName);
        } else {
            results = passengerRepository.findAll();
        }
        return results.stream().map(PassengerDTO::from).collect(Collectors.toList());
    }

    @Transactional
    public PassengerDTO create(PassengerDTO dto) {
        Passenger p = new Passenger();
        p.setFirstName(dto.getFirstName());
        p.setLastName(dto.getLastName());
        p.setAddress(dto.getAddress());
        p.setCity(dto.getCity());
        p.setCountry(dto.getCountry());
        p.setZipcode(dto.getZipcode());
        p.setTelephone(dto.getTelephone());
        p.setEmail(dto.getEmail());
        return PassengerDTO.from(passengerRepository.save(p));
    }

    /**
     * Bulk import from list — replaces PASSENGER-INSERT-MAINPROG batch job.
     * Original: processed up to 80 passengers from 8 XML files.
     * Here: accepts a JSON array (XML parsed by client or parser utility).
     */
    @Transactional
    public List<PassengerDTO> bulkImport(List<PassengerDTO> passengers) {
        return passengers.stream().map(dto -> {
            Passenger p = new Passenger();
            p.setFirstName(dto.getFirstName());
            p.setLastName(dto.getLastName());
            p.setAddress(dto.getAddress());
            p.setCity(dto.getCity());
            p.setCountry(dto.getCountry());
            p.setZipcode(dto.getZipcode());
            p.setTelephone(dto.getTelephone());
            p.setEmail(dto.getEmail());
            return PassengerDTO.from(passengerRepository.save(p));
        }).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Integer clientId) {
        passengerRepository.deleteById(clientId);
    }
}
