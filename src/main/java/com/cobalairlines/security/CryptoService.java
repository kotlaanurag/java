package com.cobalairlines.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Replaces the COBOL custom encryption in EMPLO-CRYPTO-PASS (CRYPTPGM)
 * and CRYPTO-VERIFICATION (CICS login subprogram).
 *
 * Original COBOL algorithm (CRYPTPGM / CRYPTO-VERIFICATION):
 *  1. Used EMPID characters as encryption key
 *  2. Seeded random based on ADMIDATE (admission date)
 *  3. Applied MOD-based XOR transformation per character
 *  4. Stored 8-char encrypted result in sequential PASSDOC file
 *  5. Login compared re-encrypted input against stored value
 *
 * Migration decision:
 *  The custom XOR cipher was weak and non-standard.
 *  Replaced with BCrypt (industry-standard adaptive hash).
 *  Existing COBOL-encrypted passwords cannot be migrated automatically
 *  — employees must reset passwords on first login to the new system.
 */
@Service
public class CryptoService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    /**
     * Hash a plain-text password for storage.
     * Replaces CRYPTPGM batch program that wrote to PASSDOC sequential file.
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verify a plain-text password against a stored BCrypt hash.
     * Replaces CRYPTO-VERIFICATION subprogram called from LOGIN-COB.
     *
     * Original return codes:
     *   0 = success  -> here: returns true
     *   1 = invalid  -> here: returns false
     *   2 = error    -> here: throws exception
     */
    public boolean verifyPassword(String rawPassword, String storedHash) {
        return passwordEncoder.matches(rawPassword, storedHash);
    }
}
