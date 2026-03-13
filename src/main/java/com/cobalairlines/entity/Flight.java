package com.cobalairlines.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Migrated from DB2 FLIGHT table and FLIGHT-DCLGEN copybook.
 *
 * Original COBOL DB2 definition:
 *   FLIGHTID   INT (GENERATED ALWAYS AS IDENTITY)   PK
 *   FLIGHTDATE DATE
 *   DEPTIME    TIME
 *   ARRTIME    TIME
 *   TOTPASS    INT      (total passengers)
 *   TOTBAGGA   INT      (total baggage)
 *   FLIGHTNUM  CHAR(6)
 *   SHIFTID    INT      FK -> SHIFT
 *   AIRPLANEID CHAR(8)  FK -> AIRPLANE
 *   AIRPORTDEP CHAR(4)  FK -> AIRPORT  (departure)
 *   AIRPORTARR CHAR(4)  FK -> AIRPORT  (arrival)
 *
 * Duplication logic: migrated from FLIGHT-DUPLICATE-COB (CBFLIGHT).
 * Original program replicated flights for each day of the month for 8 flight numbers.
 */
@Entity
@Table(name = "flight")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "flight_date")
    @NotNull
    private LocalDate flightDate;

    @Column(name = "dep_time")
    private LocalTime depTime;

    @Column(name = "arr_time")
    private LocalTime arrTime;

    @Column(name = "total_passengers")
    private Integer totalPassengers;

    @Column(name = "total_baggage")
    private Integer totalBaggage;

    @Column(name = "flight_num", length = 6)
    @NotBlank
    private String flightNum;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airplane_id", referencedColumnName = "airplane_id")
    private Airplane airplane;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airport_dep_id", referencedColumnName = "airport_id")
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airport_arr_id", referencedColumnName = "airport_id")
    private Airport arrivalAirport;
}
