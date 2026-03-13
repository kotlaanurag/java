package com.cobalairlines.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Migrated from DB2 BUY table.
 *
 * Original COBOL DB2 definition:
 *   BUYID     INT (GENERATED ALWAYS AS IDENTITY)   PK
 *   BUYDATE   DATE
 *   BUYTIME   TIME
 *   PRICE     DEC(7,2)
 *   EMPID     CHAR(8)   FK -> EMPLO   (the sales employee)
 *   CLIENTID  INT       FK -> PASSENGERS
 *
 * Price calculation: migrated from SELL1-COB.
 * Original hardcoded price: 120.99 per ticket.
 */
@Entity
@Table(name = "buy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Buy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buy_id")
    private Integer buyId;

    @Column(name = "buy_date")
    private LocalDate buyDate;

    @Column(name = "buy_time")
    private LocalTime buyTime;

    @Column(name = "price", precision = 7, scale = 2)
    private BigDecimal price;

    /** Sales employee who processed the transaction */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", referencedColumnName = "emp_id")
    private Employee employee;

    /** Client who made the purchase */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Passenger passenger;
}
