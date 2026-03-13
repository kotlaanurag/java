package com.cobalairlines.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Migrated from LOGINMAP BMS map fields USERID and PASSWRD.
 *
 * Original CICS:
 *   EXEC CICS RECEIVE MAP('LOGINMAP') MAPSET('LOGINMAP') END-EXEC
 *   MOVE LOGINIMP(USERIDL:USERIDL) TO WS-USERID
 *   MOVE LOGINIMP(PASSWRDL:PASSWRDL) TO WS-PASSWORD
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotBlank(message = "Password is required")
    private String password;
}
