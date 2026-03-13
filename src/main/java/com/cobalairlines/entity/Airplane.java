package com.cobalairlines.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Migrated from DB2 AIRPLANE table.
 *
 * Original COBOL DB2 definition:
 *   AIRPLANEID  CHAR(8)   PRIMARY KEY
 *   TYPE        VARCHAR(8)
 *   NUMSEATS    INT
 *   TOTALFUEL   INT
 *
 * Fleet: BOEING01 (737-200, 130 seats), AIRBUS01 (A320, 150 seats),
 *        AIRBUS02 (A340, 260 seats)
 */
@Entity
@Table(name = "airplane")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airplane {

    @Id
    @Column(name = "airplane_id", length = 8)
    @NotBlank
    private String airplaneId;

    @Column(name = "type", length = 8)
    private String type;

    @Column(name = "num_seats")
    @Positive
    private Integer numSeats;

    @Column(name = "total_fuel")
    @Positive
    private Integer totalFuel;
}
