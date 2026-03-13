package com.cobalairlines.controller;

import com.cobalairlines.dto.LoginRequest;
import com.cobalairlines.dto.LoginResponse;
import com.cobalairlines.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller — replaces LOGINMAP BMS screen + LOGIN-COB CICS program.
 *
 * Original CICS transaction: user typed userid/password on LOGINMAP green screen.
 * Migrated: HTTP POST /api/auth/login with JSON body.
 *
 * POST /api/auth/login  ->  LoginRequest  ->  LoginResponse (JWT + dept info)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Migrated from LOGIN-COB CICS program.
     * Returns JWT token + department info for client-side routing
     * (replacing CICS XCTL to department-specific programs).
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
