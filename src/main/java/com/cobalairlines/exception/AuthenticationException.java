package com.cobalairlines.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when login fails.
 * Original COBOL (LOGIN-COB):
 *   MOVE 'INVALID CREDENTIALS' TO MSGO
 *   EXEC CICS SEND MAP('LOGINMAP') MAPONLY END-EXEC
 *
 * CRYPTO-VERIFICATION return code 1 (invalid) or 2 (error).
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
