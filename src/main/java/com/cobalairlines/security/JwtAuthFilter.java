package com.cobalairlines.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT authentication filter.
 *
 * Extracts the JWT from the Authorization header and validates it,
 * replicating the session/COMMAREA validation that CICS performed
 * at the start of each pseudo-conversation program.
 *
 * Original CICS flow:
 *   EXEC CICS HANDLE CONDITION NOTFND(...) END-EXEC
 *   EXEC CICS ADDRESS COMMAREA(WS-COMMAREA) END-EXEC
 *   IF EIBCALEN = 0 THEN GO TO 0000-INIT ELSE GO TO 0100-RESUME
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String empId = jwtTokenProvider.getEmpIdFromToken(token);
            String department = jwtTokenProvider.getDepartmentFromToken(token);

            // Department name becomes the Spring Security role (ROLE_<DEPT>)
            String role = "ROLE_" + department.toUpperCase().replace(" ", "_");

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            empId,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
