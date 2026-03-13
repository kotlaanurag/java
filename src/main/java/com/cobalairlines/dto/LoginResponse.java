package com.cobalairlines.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Returned to client after successful authentication.
 *
 * Replaces CICS pseudo-conversation routing to department maps.
 * Original: LOGIN-COB routed via EXEC CICS XCTL PROGRAM(...) END-EXEC
 * based on DEPTID. Here, the JWT token carries the role (department name).
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String empId;
    private String firstName;
    private String lastName;
    private String department;
    private Integer deptId;
}
