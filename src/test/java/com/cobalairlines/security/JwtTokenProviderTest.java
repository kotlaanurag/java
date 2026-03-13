package com.cobalairlines.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "test-secret-key-for-unit-tests-only");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 3600000L);
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtTokenProvider.generateToken("EMP001", 7, "Sales");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void getEmpIdFromToken_returnsCorrectEmpId() {
        String token = jwtTokenProvider.generateToken("EMP001", 7, "Sales");
        assertThat(jwtTokenProvider.getEmpIdFromToken(token)).isEqualTo("EMP001");
    }

    @Test
    void getDeptIdFromToken_returnsCorrectDeptId() {
        String token = jwtTokenProvider.generateToken("EMP001", 7, "Sales");
        assertThat(jwtTokenProvider.getDeptIdFromToken(token)).isEqualTo(7);
    }

    @Test
    void getDepartmentFromToken_returnsCorrectDepartment() {
        String token = jwtTokenProvider.generateToken("EMP001", 7, "Sales");
        assertThat(jwtTokenProvider.getDepartmentFromToken(token)).isEqualTo("Sales");
    }

    @Test
    void validateToken_returnsTrueForValidToken() {
        String token = jwtTokenProvider.generateToken("EMP001", 7, "Sales");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_returnsFalseForInvalidToken() {
        assertThat(jwtTokenProvider.validateToken("invalid.token.value")).isFalse();
    }

    @Test
    void validateToken_returnsFalseForEmptyToken() {
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
    }
}
