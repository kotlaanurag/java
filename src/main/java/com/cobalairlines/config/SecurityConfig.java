package com.cobalairlines.config;

import com.cobalairlines.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 *
 * Maps COBOL department-based access control to Spring Security roles.
 *
 * Original CICS routing (LOGIN-COB XCTL targets):
 *   DEPTID 1  -> CEO-MAP        -> ROLE_CEO
 *   DEPTID 2  -> CMDR-MAP       -> ROLE_COMMANDER
 *   DEPTID 3  -> COPI-MAP       -> ROLE_COPILOTE
 *   DEPTID 4  -> FA-MAP         -> ROLE_FLIGHT_ATTENDANT
 *   DEPTID 5  -> HR-MAP         -> ROLE_HUMAN_RESOURCES
 *   DEPTID 6  -> IT-MAP         -> ROLE_IT_SUPPORT
 *   DEPTID 7  -> SALES-MAP      -> ROLE_SALES
 *   DEPTID 8  -> LEGAL-MAP      -> ROLE_LEGAL
 *   DEPTID 9  -> SCHEDULE-MAP   -> ROLE_SCHEDULE
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public: login endpoint
                .requestMatchers("/api/auth/**").permitAll()
                // H2 console for dev
                .requestMatchers("/h2-console/**").permitAll()

                // Sales operations — SELL1-COB, SRCHFLY-COB, SRCHTKT-COB, RECEIPT-COB
                .requestMatchers("/api/tickets/sell", "/api/tickets/receipt/**").hasRole("SALES")

                // CEO sees everything
                .requestMatchers("/api/employees/**").hasAnyRole("CEO", "HUMAN_RESOURCES", "IT_SUPPORT")
                .requestMatchers("/api/flights/duplicate").hasAnyRole("CEO", "SCHEDULE")
                .requestMatchers("/api/flights/**").authenticated()
                .requestMatchers("/api/tickets/**").authenticated()
                .requestMatchers("/api/passengers/**").authenticated()
                .requestMatchers("/api/airports/**").authenticated()
                .requestMatchers("/api/departments/**").hasAnyRole("CEO", "HUMAN_RESOURCES")

                .anyRequest().authenticated()
            )
            .headers(h -> h.frameOptions(fo -> fo.sameOrigin())) // H2 console
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
