package com.cobalairlines.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Migrated from DB2 AIRPORT table (create-db script).
 *
 * Original COBOL DB2 definition:
 *   AIRPORTID  CHAR(4)        PRIMARY KEY
 *   NAME       VARCHAR(100)   NOT NULL
 *   ADDRESS    VARCHAR(250)
 *   CITY       VARCHAR(30)
 *   COUNTRY    VARCHAR(30)
 *   ZIPCODE    VARCHAR(15)
 */
@Entity
@Table(name = "airport")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    @Id
    @Column(name = "airport_id", length = 4)
    @NotBlank
    @Size(max = 4)
    private String airportId;

    @Column(name = "name", length = 100, nullable = false)
    @NotBlank
    private String name;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "city", length = 30)
    private String city;

    @Column(name = "country", length = 30)
    private String country;

    @Column(name = "zipcode", length = 15)
    private String zipcode;
}
