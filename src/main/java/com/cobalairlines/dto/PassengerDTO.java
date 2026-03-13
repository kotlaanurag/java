package com.cobalairlines.dto;

import com.cobalairlines.entity.Passenger;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Migrated from PASSENGER XML structure parsed by PASSENGER-SUXML-SUBPROG.
 *
 * Original XML fields:
 *   FIRSTNAME, LASTNAME, ADDRESS, CITY, COUNTRY, ZIPCODE, TELEPHONE, EMAIL
 *
 * Used for:
 *  - Bulk XML import (POST /api/passengers/import) — replaces PASSENG batch job
 *  - Single passenger creation / response
 */
@Data
public class PassengerDTO {

    private Integer clientId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String address;
    private String city;
    private String country;
    private String zipcode;
    private String telephone;

    @Email
    private String email;

    public static PassengerDTO from(Passenger p) {
        PassengerDTO dto = new PassengerDTO();
        dto.setClientId(p.getClientId());
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setAddress(p.getAddress());
        dto.setCity(p.getCity());
        dto.setCountry(p.getCountry());
        dto.setZipcode(p.getZipcode());
        dto.setTelephone(p.getTelephone());
        dto.setEmail(p.getEmail());
        return dto;
    }
}
