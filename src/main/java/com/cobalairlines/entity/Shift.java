package com.cobalairlines.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Migrated from DB2 SHIFT table.
 *
 * Original COBOL DB2 definition:
 *   SHIFTID    INT (GENERATED ALWAYS AS IDENTITY)   PK
 *   SHIFTDATE  DATE
 *   BEGINTIME  TIME
 *   ENDTIME    TIME
 *   CREWID     INT   FK -> CREW
 */
@Entity
@Table(name = "shift")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shift_id")
    private Integer shiftId;

    @Column(name = "shift_date")
    private LocalDate shiftDate;

    @Column(name = "begin_time")
    private LocalTime beginTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;
}
