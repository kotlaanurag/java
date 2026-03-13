package com.cobalairlines.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Migrated from DB2 PASSENGERS table and PASSENG-DCLGEN copybook.
 *
 * Original COBOL DB2 definition:
 *   CLIENTID   INT (GENERATED ALWAYS AS IDENTITY)   PK
 *   FIRSTNAME  VARCHAR(30)    NOT NULL
 *   LASTNAME   VARCHAR(30)    NOT NULL
 *   ADDRESS    VARCHAR(250)
 *   CITY       VARCHAR(50)
 *   COUNTRY    VARCHAR(30)
 *   ZIPCODE    VARCHAR(15)
 *   TELEPHONE  VARCHAR(18)
 *   EMAIL      VARCHAR(100)
 *
 * Bulk load: migrated from PASSENGER-INSERT-MAINPROG + PASSENGER-SUXML-SUBPROG.
 * Original loaded data from 8 UTF-8 XML files (split due to mainframe 27920-char limit).
 */
@Entity
@Table(name = "passenger")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "first_name", length = 30, nullable = false)
    @NotBlank
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    @NotBlank
    private String lastName;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "country", length = 30)
    private String country;

    @Column(name = "zipcode", length = 15)
    private String zipcode;

    @Column(name = "telephone", length = 18)
    private String telephone;

    @Column(name = "email", length = 100)
    @Email
    private String email;
}
