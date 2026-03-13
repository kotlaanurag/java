package com.cobalairlines.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoServiceTest {

    private CryptoService cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoService();
    }

    @Test
    void hashPassword_returnsNonNullHash() {
        String hash = cryptoService.hashPassword("secret123");
        assertThat(hash).isNotNull().isNotBlank();
    }

    @Test
    void hashPassword_producesDifferentHashEachTime() {
        String hash1 = cryptoService.hashPassword("secret123");
        String hash2 = cryptoService.hashPassword("secret123");
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void verifyPassword_returnsTrueForCorrectPassword() {
        String hash = cryptoService.hashPassword("mypassword");
        assertThat(cryptoService.verifyPassword("mypassword", hash)).isTrue();
    }

    @Test
    void verifyPassword_returnsFalseForWrongPassword() {
        String hash = cryptoService.hashPassword("mypassword");
        assertThat(cryptoService.verifyPassword("wrongpassword", hash)).isFalse();
    }
}
