package com.example.boilerplate.shared.exceptions.types;

import org.springframework.http.HttpStatus;

public class InvalidFileException extends BaseException {

    private static final String ERROR_CODE = "INVALID_FILE";

    public InvalidFileException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_CODE);
        initCause(cause);
    }

}
