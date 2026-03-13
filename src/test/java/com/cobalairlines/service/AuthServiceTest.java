package com.cobalairlines.service;

import com.cobalairlines.dto.LoginRequest;
import com.cobalairlines.dto.LoginResponse;
import com.cobalairlines.entity.Department;
import com.cobalairlines.entity.Employee;
import com.cobalairlines.exception.AuthenticationException;
import com.cobalairlines.repository.EmployeeRepository;
import com.cobalairlines.security.CryptoService;
import com.cobalairlines.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private Employee employee;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department(7, "Sales", null);
        employee = new Employee();
        employee.setEmpId("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPasswordHash("hashed_password");
        employee.setDepartment(department);
    }

    @Test
    void login_successfulLogin_returnsLoginResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmpId("EMP001");
        request.setPassword("secret");

        when(employeeRepository.findByEmpId("EMP001")).thenReturn(Optional.of(employee));
        when(cryptoService.verifyPassword("secret", "hashed_password")).thenReturn(true);
        when(jwtTokenProvider.generateToken("EMP001", 7, "Sales")).thenReturn("jwt_token");

        LoginResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt_token");
        assertThat(response.getEmpId()).isEqualTo("EMP001");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getDepartment()).isEqualTo("Sales");
    }

    @Test
    void login_employeeNotFound_throwsAuthenticationException() {
        LoginRequest request = new LoginRequest();
        request.setEmpId("UNKNOWN");
        request.setPassword("secret");

        when(employeeRepository.findByEmpId("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void login_wrongPassword_throwsAuthenticationException() {
        LoginRequest request = new LoginRequest();
        request.setEmpId("EMP001");
        request.setPassword("wrongpassword");

        when(employeeRepository.findByEmpId("EMP001")).thenReturn(Optional.of(employee));
        when(cryptoService.verifyPassword("wrongpassword", "hashed_password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid credentials");
    }
}
