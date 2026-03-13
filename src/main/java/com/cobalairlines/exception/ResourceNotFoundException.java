package com.cobalairlines.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a DB2 query returns SQLCODE = 100 (NOT FOUND).
 * Original COBOL:
 *   EVALUATE SQLCODE
 *     WHEN 100 MOVE 'RECORD NOT FOUND' TO MSGO
 *   END-EVALUATE
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
