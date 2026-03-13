package com.cobalairlines.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT token management.
 *
 * Replaces CICS pseudo-conversation state management (COMMAREA / TWA).
 *
 * Original COBOL:
 *   EXEC CICS RETURN TRANSID('SELL') COMMAREA(WS-COMMAREA) END-EXEC
 *   -- COMMAREA carried: userid, deptid, session data
 *
 * JWT carries: empId, deptId, departmentName as claims.
 * Department determines which API endpoints the employee can access
 * (replaces XCTL routing to department-specific CICS programs).
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String empId, Integer deptId, String departmentName) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(empId)
                .claim("deptId", deptId)
                .claim("department", departmentName)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmpIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Integer getDeptIdFromToken(String token) {
        return parseClaims(token).get("deptId", Integer.class);
    }

    public String getDepartmentFromToken(String token) {
        return parseClaims(token).get("department", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
