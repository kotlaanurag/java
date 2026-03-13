package com.cobalairlines;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class CobalAirlinesApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context starts correctly with the H2 profile.
        // Equivalent to checking that CICS/DB2 connections are initialized in original system.
    }
}
