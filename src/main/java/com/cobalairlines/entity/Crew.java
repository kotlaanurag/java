package com.cobalairlines.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Migrated from DB2 CREW table.
 *
 * Original COBOL DB2 definition:
 *   CREWID          INT (GENERATED ALWAYS AS IDENTITY)   PK
 *   COMMANDER       CHAR(8)   FK -> EMPLO
 *   COPILOTE        CHAR(8)   FK -> EMPLO
 *   FACHIEF         CHAR(8)   FK -> EMPLO   (Flight Attendant Chief)
 *   FLIATTENDANT1   CHAR(8)   FK -> EMPLO
 *   FLIATTENDANT2   CHAR(8)   FK -> EMPLO
 *   FLIATTENDANT3   CHAR(8)   FK -> EMPLO
 */
@Entity
@Table(name = "crew")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
    private Integer crewId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commander_id", referencedColumnName = "emp_id")
    private Employee commander;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "copilote_id", referencedColumnName = "emp_id")
    private Employee copilote;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fa_chief_id", referencedColumnName = "emp_id")
    private Employee faChief;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_attendant1_id", referencedColumnName = "emp_id")
    private Employee flightAttendant1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_attendant2_id", referencedColumnName = "emp_id")
    private Employee flightAttendant2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_attendant3_id", referencedColumnName = "emp_id")
    private Employee flightAttendant3;
}
