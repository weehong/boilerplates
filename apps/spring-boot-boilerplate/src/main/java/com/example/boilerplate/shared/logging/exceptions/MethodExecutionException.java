package com.example.boilerplate.shared.logging.exceptions;

public class MethodExecutionException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Execution failed for method %s after %d ms";

    public MethodExecutionException(String methodName, long executionTimeMs, Throwable cause) {
        super(String.format(MESSAGE_FORMAT, methodName, executionTimeMs), cause);
    }

}
