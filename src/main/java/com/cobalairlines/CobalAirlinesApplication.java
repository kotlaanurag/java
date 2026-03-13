package com.cobalairlines;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Migrated from COBOL-AIRLINES mainframe application.
 *
 * Original system used:
 *   - IBM COBOL + DB2 (batch programs)
 *   - CICS + BMS (online transactions)
 *   - AS/400 display files (green-screen UI)
 *
 * This Spring Boot application replaces all CICS/batch programs with
 * RESTful services and a standard relational database (PostgreSQL / H2).
 */
@SpringBootApplication
public class CobalAirlinesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CobalAirlinesApplication.class, args);
    }
}
