package com.cobalairlines.repository;

import com.cobalairlines.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Replaces DB2 queries in SELL1-COB and SRCHTKT-COB.
 *
 * Original COBOL (SELL1-COB):
 *   EXEC SQL SELECT FIRSTNAME, LASTNAME INTO :WS-FIRSTNAME, :WS-LASTNAME
 *            FROM PASSENGERS WHERE CLIENTID = :WS-CLIENTID END-EXEC
 */
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

    List<Passenger> findByLastNameIgnoreCase(String lastName);

    List<Passenger> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    List<Passenger> findByCity(String city);
}
