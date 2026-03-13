package com.cobalairlines.service;

import com.cobalairlines.dto.LoginRequest;
import com.cobalairlines.dto.LoginResponse;
import com.cobalairlines.entity.Employee;
import com.cobalairlines.exception.AuthenticationException;
import com.cobalairlines.repository.EmployeeRepository;
import com.cobalairlines.security.CryptoService;
import com.cobalairlines.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Authentication service — migrated from LOGIN-COB (CICS) and
 * CRYPTO-VERIFICATION subprogram.
 *
 * Original COBOL flow (LOGIN-COB):
 *   1. EXEC CICS RECEIVE MAP('LOGINMAP') END-EXEC
 *   2. EXEC SQL SELECT ... FROM EMPLO WHERE EMPID = :WS-USERID END-EXEC
 *      SQLCODE check: 0=found, 100=not found
 *   3. CALL 'CRYPTVRFY' USING WS-USERID WS-PASSWORD WS-ADMDATE WS-RESULT
 *   4. IF WS-RESULT = 0
 *        EXEC CICS XCTL PROGRAM(dept-program) END-EXEC
 *      ELSE
 *        MOVE 'INVALID CREDENTIALS' TO MSGO
 *
 * Original CRYPTO-VERIFICATION return codes:
 *   0 = valid, 1 = invalid password, 2 = system error
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final CryptoService cryptoService;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        // Step 2: SQL SELECT FROM EMPLO WHERE EMPID = :WS-USERID
        Employee employee = employeeRepository.findByEmpId(request.getEmpId())
                .orElseThrow(() -> {
                    log.warn("Login failed: employee {} not found", request.getEmpId());
                    return new AuthenticationException("Invalid credentials");
                });

        // Step 3+4: CALL 'CRYPTVRFY' — compare passwords
        if (!cryptoService.verifyPassword(request.getPassword(), employee.getPasswordHash())) {
            log.warn("Login failed: invalid password for employee {}", request.getEmpId());
            throw new AuthenticationException("Invalid credentials");
        }

        String department = employee.getDepartment().getName();
        Integer deptId = employee.getDepartment().getDeptId();

        // Issue JWT — replaces CICS COMMAREA + XCTL routing to dept map
        String token = jwtTokenProvider.generateToken(employee.getEmpId(), deptId, department);

        log.info("Employee {} ({}) logged in — dept: {}", employee.getEmpId(),
                employee.getFirstName(), department);

        return new LoginResponse(token, employee.getEmpId(),
                employee.getFirstName(), employee.getLastName(), department, deptId);
    }
}
